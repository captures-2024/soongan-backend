package com.soongan.soonganbackend.soonganapi.service.member

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.SignedJWT
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.LoginRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.LoginResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.MemberInfoResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.RefreshRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.UpdateNicknameResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenAdaptor
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganapi.service.gcp.GcpStorageService
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.dto.MemberDetail
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import com.soongan.soonganbackend.soonganweb.resolver.JwtHandler
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class MemberService(
    private val memberAdapter: MemberAdapter,
    private val fcmTokenAdaptor: FcmTokenAdaptor,
    private val jwtHandler: JwtHandler,
    private val gcpStorageService: GcpStorageService,
    private val restTemplate: RestTemplate,
    private val env: Environment,
) {

    @Transactional
    fun login(userAgentEnum: UserAgentEnum, loginDto: LoginRequestDto): LoginResponseDto {
        val provider = loginDto.provider
        val idToken = loginDto.idToken

        val memberEmail = when (provider) {
            ProviderEnum.GOOGLE -> getGoogleMemberEmail(userAgentEnum, idToken)
            ProviderEnum.KAKAO -> getKakaoMemberEmail(idToken)
            ProviderEnum.APPLE -> getAppleMemberEmail(idToken)
        }

        val member = memberAdapter.getByEmail(memberEmail)
            ?: memberAdapter.save(
                MemberEntity(
                    email = memberEmail,
                    providerEnum = provider,
                    authorities = "ROLE_MEMBER"
                )
            )

        fcmTokenAdaptor.findByToken(loginDto.fcmToken)?.let { foundFcmToken ->
            if (foundFcmToken.member == null || foundFcmToken.member!!.id != member.id) {
                fcmTokenAdaptor.save(foundFcmToken.copy(id = foundFcmToken.id, member = member))
            }
        } ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_FCM_TOKEN)

        val issuedTokens = jwtHandler.issueTokens(member.email, member.authorities.split(","))
        return LoginResponseDto(
            accessToken = issuedTokens.first,
            refreshToken = issuedTokens.second
        )
    }

    fun getMemberInfo(loginMember: MemberEntity): MemberInfoResponseDto {
        return MemberInfoResponseDto.from(loginMember)
    }

    fun getGoogleMemberEmail(userAgentEnum: UserAgentEnum, idToken: String): String {
        val clientId = when (userAgentEnum) {
            UserAgentEnum.ANDROID -> env.getProperty("oauth2.android.google.client-id")
            UserAgentEnum.IOS -> env.getProperty("oauth2.ios.google.client-id")
        }
        val verifier = GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory())
            .setAudience(listOf(clientId))
            .build()


        try {
            val verifiedIdToken = verifier.verify(idToken)
                ?: throw SoonganException(  // 토큰이 유효하지 않은 경우
                    StatusCode.INVALID_OAUTH2_ID_TOKEN,
                    "Google IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다."
                )

            val email = verifiedIdToken.payload.email
            return email as String
        } catch (e: IllegalArgumentException) {  // 토큰 자체 형식이 맞지 않아 해독 도중 에러가 발생한 경우
            throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "잘못된 Google IdToken 형식으로 회원 정보를 가져올 수 없습니다.")
        }
    }

    fun getKakaoMemberEmail(idToken: String): String {
        val url = "https://kapi.kakao.com/v2/user/me"
        val headers = mapOf(
            "Authorization" to "Bearer $idToken"
        )

        val kakaoUserResponse = restTemplate.getForObject<Map<*, *>>(url, headers)
        return kakaoUserResponse["kakao_account"]?.let { kakaoAccount ->
            (kakaoAccount as Map<*, *>)["email"] as String
        } ?: throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Kakao IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")
    }

    fun getAppleMemberEmail(idToken: String): String {
        val applePublicKeysUrl = "https://appleid.apple.com/auth/keys"
        val applePublicKeySets = restTemplate.getForObject<Map<*, *>>(
            applePublicKeysUrl
        )["keys"] as List<*>

        val signedJWT = SignedJWT.parse(idToken)
        val header = signedJWT.header as JWSHeader
        val kid = header.keyID

        val applePublicKeySet = applePublicKeySets.find { keySet ->
            val keySetMap = keySet as Map<*, *>
            keySetMap["kid"] == kid
        }?.let {
            it as Map<*, *>
        } ?: throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Applie IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")

        val rsaKey = RSAKey.Builder(
            Base64URL(applePublicKeySet["n"] as String),
            Base64URL(applePublicKeySet["e"] as String)
        ).build()

        val verifier = RSASSAVerifier(rsaKey)
        if (!signedJWT.verify(verifier)) {
            throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Applie IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")
        }

        val claims = signedJWT.jwtClaimsSet
        return claims.getStringClaim("email")
    }

    fun logout(loginMember: MemberDetail) {
        try {
            jwtHandler.deleteToken(loginMember.email)
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

        val issuedTokens = jwtHandler.issueTokens(member.email, member.authorities.split(","))
        return LoginResponseDto(
            accessToken = issuedTokens.first,
            refreshToken = issuedTokens.second
        )
    }

    @Transactional(readOnly = true)
    fun checkNickname(nickname: String): Boolean {
        return memberAdapter.getByNickname(nickname) == null
    }

    @Transactional
    fun updateNickname(loginMember: MemberEntity, newNickname: String): UpdateNicknameResponseDto {
        val updatedMember = loginMember.copy(nickname = newNickname)
        memberAdapter.save(updatedMember)

        return UpdateNicknameResponseDto(
            memberEmail = updatedMember.email,
            updatedNickname = newNickname
        )
    }

    @Transactional
    fun updateProfileImage(loginMember: MemberEntity, profileImage: MultipartFile) {
        if (loginMember.profileImageUrl != null) {
            gcpStorageService.deleteFile(loginMember.profileImageUrl!!)
        }

        val updatedProfileImageUrl = gcpStorageService.uploadFile(profileImage, loginMember.id!!)
        val updatedMember = loginMember.copy(profileImageUrl = updatedProfileImageUrl)
        memberAdapter.save(updatedMember)
    }
}
