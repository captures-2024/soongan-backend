package com.soongan.soonganbackend.service.member

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.gson.Gson
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.SignedJWT
import com.soongan.soonganbackend.enums.Provider
import com.soongan.soonganbackend.enums.TokenType
import com.soongan.soonganbackend.enums.UserAgent
import com.soongan.soonganbackend.interfaces.member.dto.*
import com.soongan.soonganbackend.persistence.member.MemberAdapter
import com.soongan.soonganbackend.service.jwt.JwtService
import com.soongan.soonganbackend.persistence.member.MemberEntity
import com.soongan.soonganbackend.service.gcp.GcpStorageService
import com.soongan.soonganbackend.util.common.dto.MemberDetail
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberAdapter: MemberAdapter,
    private val jwtService: JwtService,
    private val env: Environment,
    private val gcpStorageService: GcpStorageService
) {

    private val httpClient = OkHttpClient()
    private val gson = Gson()

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

        val issuedTokens = jwtService.issueTokens(member.email, member.authorities.split(","))
        return LoginResponseDto(
            accessToken = issuedTokens.first,
            refreshToken = issuedTokens.second
        )
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
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $idToken")
            .build()

        val response = httpClient.newCall(request).execute()
        val userInfo = gson.fromJson(response.body?.string(), Map::class.java)["kakao_account"] as Map<*, *>
        val email = userInfo["email"]
            ?: throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Kakao IdToken이 유효하지 않아 회원 정보를 가져올 수 ���습니다.")
        return email as String
    }

    fun getAppleMemberEmail(idToken: String): String {
        val applePublicKeysUrl = "https://appleid.apple.com/auth/keys"
        val request = Request.Builder()
            .url(applePublicKeysUrl)
            .build()

        val response = httpClient.newCall(request).execute()
        val applePublicKeySets = gson.fromJson(response.body?.string(), Map::class.java)["keys"] as List<Map<*, *>>

        val signedJWT = SignedJWT.parse(idToken)
        val header = signedJWT.header as JWSHeader
        val kid = header.keyID

        val applePublicKeySet = applePublicKeySets.find { it["kid"] == kid }
            ?: throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Applie IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")

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

    fun logout(loginMember: MemberDetail): LogoutResponseDto {
        try {
            jwtService.deleteToken(loginMember.email)
        } catch (e: Exception) {
            throw SoonganException(StatusCode.INVALID_JWT_TOKEN, "로그아웃에 실패했습니다.")
        }

        return LogoutResponseDto(
            memberEmail = loginMember.email,
            message = "로그아웃 성공"
        )
    }

    fun withdraw(loginMember: MemberDetail): WithdrawResponseDto {
        try {
            memberAdapter.deleteByEmail(loginMember.email)
            jwtService.deleteToken(loginMember.email)
            gcpStorageService.deleteMemberFiles(loginMember.id)
        } catch (e: Exception) {
            throw SoonganException(StatusCode.SERVICE_NOT_AVAILABLE, "회원 탈퇴에 실패했습니다.")
        }

        return WithdrawResponseDto(
            memberEmail = loginMember.email,
            message = "회원 탈퇴 성공"
        )
    }

    fun refresh(refreshRequestDto: RefreshRequestDto): LoginResponseDto {
        val refreshTokenPayload = jwtService.getPayload(refreshRequestDto.refreshToken, TokenType.REFRESH)
        val memberEmail = refreshTokenPayload["sub"] as String
        val member = memberAdapter.getByEmail(memberEmail)
            ?: throw SoonganException(StatusCode.INVALID_JWT_TOKEN, "회원 정보를 찾을 수 없습니다.")

        val issuedTokens = jwtService.issueTokens(member.email, member.authorities.split(","))
        return LoginResponseDto(
            accessToken = issuedTokens.first,
            refreshToken = issuedTokens.second
        )
    }

    fun checkNickname(nickname: String): Boolean {
        return memberAdapter.getByNickname(nickname) == null
    }

    fun updateNickname(loginMember: MemberDetail, newNickname: String): UpdateNicknameResponseDto {
        val member = memberAdapter.getByEmail(loginMember.email)
            ?: throw SoonganException(StatusCode.INVALID_JWT_TOKEN, "회원 정보를 찾을 수 없습니다.")

        member.nickname = newNickname
        memberAdapter.save(member)

        return UpdateNicknameResponseDto(
            memberEmail = loginMember.email,
            updatedNickname = newNickname,
            message = "닉네임 변경 성공"
        )
    }
}
