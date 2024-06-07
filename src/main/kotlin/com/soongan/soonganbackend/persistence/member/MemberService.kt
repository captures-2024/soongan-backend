package com.soongan.soonganbackend.persistence.member

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.gson.Gson
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.SignedJWT
import com.soongan.soonganbackend.dto.LoginDto
import com.soongan.soonganbackend.dto.LoginResultDto
import com.soongan.soonganbackend.enums.Provider
import com.soongan.soonganbackend.exception.token.InvalidOAuth2IdTokenException
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val jwtService: JwtService,
    private val env: Environment
) {

    private val httpClient = OkHttpClient()
    private val gson = Gson()

    fun login(loginDto: LoginDto): LoginResultDto {
        val provider = loginDto.provider
        val idToken = loginDto.idToken

        val providerEmail = when (provider) {
            Provider.GOOGLE -> getGoogleMemberEmail(idToken)
            Provider.KAKAO -> getKakaoMemberEmail(idToken)
            Provider.APPLE -> getAppleMemberEmail(idToken)
        }

        val member = memberRepository.findByEmail(providerEmail)
            ?: memberRepository.save(MemberEntity(
                email = providerEmail,
                provider = provider,
                authorities = "ROLE_MEMBER"
            ))

        val issuedTokens = jwtService.issueTokens(member.email, member.authorities.split(","))
        return LoginResultDto(
            accessToken = issuedTokens.first,
            refreshToken = issuedTokens.second
        )
    }

    fun getGoogleMemberEmail(idToken: String): String {
        val verifier = GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory())
            .setAudience(listOf(env.getProperty("oauth2.google.client-id")))
            .build()
        val verifiedIdToken = verifier.verify(idToken) ?: throw InvalidOAuth2IdTokenException("Google IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")
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
        val email = userInfo["email"] ?: InvalidOAuth2IdTokenException("Kakao IdToken이 유효하지 않아 회원 정보를 가져올 수 ���습니다.")
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
            ?: throw InvalidOAuth2IdTokenException("Applie IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")

        val rsaKey = RSAKey.Builder(
            Base64URL(applePublicKeySet["n"] as String),
            Base64URL(applePublicKeySet["e"] as String)
        ).build()

        val verifier = RSASSAVerifier(rsaKey)
        if (!signedJWT.verify(verifier)) {
            throw InvalidOAuth2IdTokenException("Applie IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")
        }

        val claims = signedJWT.jwtClaimsSet
        return claims.getStringClaim("email")
    }
}