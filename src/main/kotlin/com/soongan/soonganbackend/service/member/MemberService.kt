package com.soongan.soonganbackend.service.member

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.SignedJWT
import com.soongan.soonganbackend.enums.Provider
import com.soongan.soonganbackend.enums.UserAgent
import com.soongan.soonganbackend.interfaces.member.dto.*
import com.soongan.soonganbackend.persistence.fcm.FcmTokenAdaptor
import com.soongan.soonganbackend.persistence.member.MemberAdapter
import com.soongan.soonganbackend.service.jwt.JwtService
import com.soongan.soonganbackend.persistence.member.MemberEntity
import com.soongan.soonganbackend.service.gcp.GcpStorageService
import com.soongan.soonganbackend.util.common.dto.MemberDetail
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
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
    private val jwtService: JwtService,
    private val gcpStorageService: GcpStorageService,
    private val restTemplate: RestTemplate,
    private val env: Environment,
) {

    @Transactional
    fun login(userAgent: UserAgent, loginDto: LoginRequestDto): LoginResponseDto {
        val provider = loginDto.provider
        val idToken = loginDto.idToken

        val memberEmail = when (provider) {
            Provider.GOOGLE -> getGoogleMemberEmail(userAgent, idToken)
            Provider.KAKAO -> getKakaoMemberEmail(idToken)
            Provider.APPLE -> getAppleMemberEmail(idToken)
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
            if (foundFcmToken.member == null || foundFcmToken.member.id != member.id) {
                fcmTokenAdaptor.save(foundFcmToken.copy(member = member))
            }
        } ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_FCM_TOKEN)

        val issuedTokens = jwtService.issueTokens(member.email, member.authorities.split(","))
        return LoginResponseDto(
            accessToken = issuedTokens.first,
            refreshToken = issuedTokens.second
        )
    }

    fun getMemberInfo(loginMember: MemberEntity): MemberInfoResponseDto {
        return MemberInfoResponseDto.from(loginMember)
    }

    fun getGoogleMemberEmail(userAgent: UserAgent, idToken: String): String {
        val clientId = when (userAgent) {
            UserAgent.ANDROID -> env.getProperty("oauth2.android.google.client-id")
            UserAgent.IOS -> env.getProperty("oauth2.ios.google.client-id")
        }
        val verifier = GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory())
            .setAudience(listOf(clientId))
            .build()
        val verifiedIdToken = verifier.verify(idToken)
            ?: throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Google IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")
        val email = verifiedIdToken.payload.email
        return email as String
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
            jwtService.deleteToken(loginMember.email)
        } catch (e: Exception) {
            throw SoonganException(StatusCode.SOONGAN_API_FAIL_TO_LOGOUT)
        }
    }

    @Transactional
    fun withdraw(loginMember: MemberEntity) {
        val softDeletedMember = loginMember.copy(withdrawalAt = LocalDateTime.now())
        memberAdapter.save(softDeletedMember)
        jwtService.deleteToken(loginMember.email)
    }

    @Transactional
    fun refresh(refreshRequestDto: RefreshRequestDto): LoginResponseDto {
        val payload = jwtService.validateRefreshRequest(refreshRequestDto)
        val memberEmail = payload["sub"] as String
        val member = memberAdapter.getByEmail(memberEmail)
            ?: throw SoonganException(StatusCode.NOT_FOUND_MEMBER_BY_EMAIL)

        val issuedTokens = jwtService.issueTokens(member.email, member.authorities.split(","))
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
            gcpStorageService.deleteFile(loginMember.profileImageUrl)
        }

        val updatedProfileImageUrl = gcpStorageService.uploadFile(profileImage, loginMember.id!!)
        val updatedMember = loginMember.copy(profileImageUrl = updatedProfileImageUrl)
        memberAdapter.save(updatedMember)
    }
}
