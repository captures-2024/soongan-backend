package com.soongan.soonganbackend.soonganapi.service.auth

import com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.request.LoginRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.response.LoginResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.request.RefreshRequestDto
import com.soongan.soonganbackend.soonganapi.service.auth.validator.AppleOAuth2Validator
import com.soongan.soonganbackend.soonganapi.service.auth.validator.GoogleOAuth2Validator
import com.soongan.soonganbackend.soonganapi.service.auth.validator.KakaoOAuth2Validator
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import com.soongan.soonganbackend.soonganweb.resolver.JwtHandler
import com.soongan.soonganbackend.soongansupport.util.constant.MdcConstant
import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.MDC
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class AuthService(
    private val memberAdapter: MemberAdapter,
    private val fcmTokenAdapter: FcmTokenAdapter,
    private val jwtHandler: JwtHandler,
    private val googleOAuth2Validator: GoogleOAuth2Validator,
    private val kakaoOAuth2Validator: KakaoOAuth2Validator,
    private val appleOAuth2Validator: AppleOAuth2Validator
) {
    private val logger = KotlinLogging.logger {}

    private fun getClientIp(): String = MDC.get(MdcConstant.CLIENT_IP) ?: "unknown"

    private fun hashToken(token: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(token.toByteArray())
            hash.joinToString("") { "%02x".format(it) }.substring(0, 8)
        } catch (e: Exception) {
            "hash_error"
        }
    }

    @Transactional
    fun login(userAgent: UserAgentEnum, loginDto: LoginRequestDto): LoginResponseDto {
        val clientIp = getClientIp()
        logger.info { "[Auth] login_attempt user={unknown}, provider=${loginDto.provider}, ip=$clientIp, userAgent=$userAgent" }

        val provider = loginDto.provider
        val idToken = loginDto.idToken

        val oauthValidateResult = try {
            when (provider) {
                ProviderEnum.GOOGLE -> googleOAuth2Validator.validateTokenAndGetEmail(idToken, userAgent)
                ProviderEnum.KAKAO -> kakaoOAuth2Validator.validateTokenAndGetEmail(idToken)
                ProviderEnum.APPLE -> appleOAuth2Validator.validateTokenAndGetEmail(idToken)
            }
        } catch (e: Exception) {
            logger.warn { "[Auth] login_failed user={unknown}, provider=$provider, ip=$clientIp, reason=OAuth validation failed (${e.message})" }
            throw e
        }

        var member = memberAdapter.getByProviderAndProviderId(provider = provider, providerId = oauthValidateResult.providerId)
        if (member != null) {
            this.checkMember(member)
        } else {
            val sameEmailMember = memberAdapter.getByEmail(oauthValidateResult.email)
            if (sameEmailMember != null) {
                logger.warn { "[Auth] login_failed user={unknown}, provider=$provider, ip=$clientIp, reason=different provider (${sameEmailMember.provider})" }
                throw SoonganException(StatusCode.SOONGAN_API_DIFFERENT_PROVIDER, "해당 이메일은 ${sameEmailMember.provider}로 가입된 회원입니다.")
            }

            // 회원이 존재하지 않는 경우, 새로 생성
            member = memberAdapter.save(
                MemberEntity(
                    email = oauthValidateResult.email,
                    provider = provider,
                    providerId =  oauthValidateResult.providerId,
                )
            )
            logger.info { "[Auth] register user=${member.id}, provider=${member.provider}, ip=$clientIp" }
        }

        fcmTokenAdapter.findByToken(loginDto.fcmToken)?.let { foundFcmToken ->
            if (foundFcmToken.member == null || foundFcmToken.member!!.id != member.id) {
                fcmTokenAdapter.save(foundFcmToken.copy(id = foundFcmToken.id, member = member))
            }
        } ?: run {
            logger.warn { "[Auth] login_failed user=${member.id}, provider=$provider, ip=$clientIp, reason=FCM token not found" }
            throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_FCM_TOKEN)
        }

        val issuedTokens = jwtHandler.issueTokens(member.email)
        logger.info { "[Auth] login_success user=${member.id}, provider=${member.provider}, ip=$clientIp, userAgent=$userAgent" }
        return LoginResponseDto(
            accessToken = issuedTokens.first,
            refreshToken = issuedTokens.second
        )
    }

    fun logout(loginMemberEmail: String) {
        val clientIp = getClientIp()
        val member = memberAdapter.getByEmail(loginMemberEmail)
        val userId = member?.id ?: "unknown"

        logger.info { "[Auth] logout_attempt user=$userId, ip=$clientIp" }
        try {
            jwtHandler.deleteToken(loginMemberEmail)
            logger.info { "[Auth] logout_success user=$userId, ip=$clientIp" }
        } catch (e: Exception) {
            logger.error(e) { "[Auth] logout_failed user=$userId, ip=$clientIp, reason=${e.message}" }
            throw SoonganException(StatusCode.SOONGAN_MEMBER_API_FAIL_TO_LOGOUT)
        }
    }

    @Transactional
    fun withdraw(loginMember: MemberEntity) {
        val clientIp = getClientIp()
        logger.info { "[Auth] withdraw_attempt user=${loginMember.id}, ip=$clientIp" }

        val softDeletedMember = loginMember.copy(withdrawalAt = LocalDateTime.now())
        memberAdapter.save(softDeletedMember)
        jwtHandler.deleteToken(loginMember.email)

        logger.info { "[Auth] withdraw_success user=${loginMember.id}, ip=$clientIp" }
    }

    @Transactional
    fun refresh(refreshRequestDto: RefreshRequestDto): LoginResponseDto {
        val clientIp = getClientIp()
        val accessTokenHash = hashToken(refreshRequestDto.accessToken)

        logger.debug { "[Auth] refresh_attempt ip=$clientIp, accessTokenHash=$accessTokenHash" }

        val payload = try {
            jwtHandler.validateRefreshRequest(refreshRequestDto.accessToken, refreshRequestDto.refreshToken)
        } catch (e: Exception) {
            val reason = when {
                e.message?.contains("expired") == true -> "JWT expired"
                e.message?.contains("invalid") == true -> "JWT invalid"
                e.message?.contains("malformed") == true -> "JWT malformed"
                else -> "JWT validation failed"
            }
            logger.warn { "[Auth] refresh_failed user={unknown}, ip=$clientIp, accessTokenHash=$accessTokenHash, reason=$reason" }
            throw e
        }

        val memberEmail = payload["sub"] as String
        val member = memberAdapter.getByEmail(memberEmail)
            ?: run {
                logger.warn { "[Auth] refresh_failed user={unknown}, ip=$clientIp, accessTokenHash=$accessTokenHash, reason=member not found" }
                throw SoonganException(StatusCode.SOONGAN_MEMBER_NOT_FOUND_MEMBER_BY_EMAIL)
            }

        this.checkMember(member)

        val issuedTokens = jwtHandler.issueTokens(member.email)
        val newAccessTokenHash = hashToken(issuedTokens.first)
        logger.info { "[Auth] refresh_success user=${member.id}, ip=$clientIp, oldTokenHash=$accessTokenHash, newTokenHash=$newAccessTokenHash" }
        return LoginResponseDto(
            accessToken = issuedTokens.first,
            refreshToken = issuedTokens.second
        )
    }

    private fun checkMember(member: MemberEntity) {
        val clientIp = getClientIp()

        member.banUntil?.let { banUntil ->
            if (banUntil > LocalDateTime.now()) {
                val formattedBanUntil = banUntil.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"))
                logger.warn { "[Auth] member_check_failed user=${member.id}, ip=$clientIp, reason=banned until $formattedBanUntil" }
                throw SoonganException(StatusCode.SOONGAN_API_BANNED_MEMBER, "해당 회원은 ${formattedBanUntil}까지 이용이 제한된 상태입니다.")
            }
        }

        member.withdrawalAt?.let { withdrawalAt ->
            val formattedWithdrawalAt = withdrawalAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"))
            logger.warn { "[Auth] member_check_failed user=${member.id}, ip=$clientIp, reason=withdrawn at $formattedWithdrawalAt" }
            throw SoonganException(StatusCode.SOONGAN_API_WITHDRAWN_MEMBER, "해당 회원은 ${formattedWithdrawalAt}에 탈퇴한 회원입니다.")
        }
    }
}
