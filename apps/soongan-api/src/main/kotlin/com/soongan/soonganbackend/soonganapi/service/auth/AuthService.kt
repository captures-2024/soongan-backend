package com.soongan.soonganbackend.soonganapi.service.auth

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.SignedJWT
import com.soongan.soonganbackend.soonganapi.interfaces.auth.request.LoginRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.auth.response.LoginResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.auth.request.RefreshRequestDto
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenAdaptor
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.dto.MemberDetail
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import com.soongan.soonganbackend.soonganweb.resolver.JwtHandler
import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.LocalDateTime

@Service
class AuthService(
    private val memberAdapter: MemberAdapter,
    private val fcmTokenAdaptor: FcmTokenAdaptor,
    private val jwtHandler: JwtHandler,
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
                    provider = provider,
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

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $idToken")
        }
        val request = HttpEntity<Unit>(headers)
        return try {
            val response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                KakaoUserResponse::class.java
            )

            response.body?.kakaoAccount?.email
                ?: throw SoonganException(
                    StatusCode.INVALID_OAUTH2_ID_TOKEN,
                    "카카오 이메일 정보를 찾을 수 없습니다."
                )
        } catch (e: RestClientException) {
            throw SoonganException(
                StatusCode.INVALID_OAUTH2_ID_TOKEN,
                "카카오 API 호출 중 오류가 발생했습니다: ${e.message}"
            )
        }
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
}

data class KakaoUserResponse(
    val id: Long,
    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount?
)

data class KakaoAccount(
    val email: String?,
    @JsonProperty("email_verified")
    val emailVerified: Boolean?,
    @JsonProperty("has_email")
    val hasEmail: Boolean?
)