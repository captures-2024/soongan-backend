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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

    @Transactional
    fun login(userAgent: UserAgentEnum, loginDto: LoginRequestDto): LoginResponseDto {
        val provider = loginDto.provider
        val idToken = loginDto.idToken

        val memberEmail = when (provider) {
            ProviderEnum.GOOGLE -> googleOAuth2Validator.validateTokenAndGetEmail(idToken, userAgent)
            ProviderEnum.KAKAO -> kakaoOAuth2Validator.validateTokenAndGetEmail(idToken)
            ProviderEnum.APPLE -> appleOAuth2Validator.validateTokenAndGetEmail(idToken)
        }

        val member = memberAdapter.getByEmail(memberEmail)
            ?: memberAdapter.save(
                MemberEntity(
                    email = memberEmail,
                    provider = provider,
                )
            )

        member.banUntil?.let { banUntil ->
            if (banUntil > LocalDateTime.now()) {
                val formattedBanUntil = banUntil.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"))
                throw SoonganException(StatusCode.SOONGAN_API_BANNED_MEMBER, "해당 회원은 ${formattedBanUntil}까지 이용이 제한된 상태입니다.")
            }
        }

        fcmTokenAdapter.findByToken(loginDto.fcmToken)?.let { foundFcmToken ->
            if (foundFcmToken.member == null || foundFcmToken.member!!.id != member.id) {
                fcmTokenAdapter.save(foundFcmToken.copy(id = foundFcmToken.id, member = member))
            }
        } ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_FCM_TOKEN)

        val issuedTokens = jwtHandler.issueTokens(member.email)
        return LoginResponseDto(
            accessToken = issuedTokens.first,
            refreshToken = issuedTokens.second
        )
    }

    fun logout(loginMemberEmail: String) {
        try {
            jwtHandler.deleteToken(loginMemberEmail)
        } catch (e: Exception) {
            throw SoonganException(StatusCode.SOONGAN_API_FAIL_TO_LOGOUT)
        }
    }

    @Transactional
    fun withdraw(loginMember: MemberEntity) {
        val softDeletedMember = loginMember.copy(withdrawalAt = LocalDateTime.now())
        memberAdapter.save(softDeletedMember)
        jwtHandler.deleteToken(loginMember.email)
    }

    @Transactional
    fun refresh(refreshRequestDto: RefreshRequestDto): LoginResponseDto {
        val payload = jwtHandler.validateRefreshRequest(refreshRequestDto.accessToken, refreshRequestDto.refreshToken)
        val memberEmail = payload["sub"] as String
        val member = memberAdapter.getByEmail(memberEmail)
            ?: throw SoonganException(StatusCode.NOT_FOUND_MEMBER_BY_EMAIL)

        val issuedTokens = jwtHandler.issueTokens(member.email)
        return LoginResponseDto(
            accessToken = issuedTokens.first,
            refreshToken = issuedTokens.second
        )
    }
}