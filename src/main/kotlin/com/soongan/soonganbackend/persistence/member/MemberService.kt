package com.soongan.soonganbackend.persistence.member

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.soongan.soonganbackend.dto.LoginDto
import com.soongan.soonganbackend.dto.LoginResultDto
import com.soongan.soonganbackend.enums.Provider
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val jwtService: JwtService,
    private val env: Environment
) {
    fun login(loginDto: LoginDto): LoginResultDto {
        val provider = loginDto.provider
        val idToken = loginDto.idToken

        val memberInfo = when (provider) {
            Provider.GOOGLE -> getGoogleMemberInfo(idToken)
            Provider.KAKAO -> getKakaoMemberInfo(idToken)
            Provider.APPLE -> getAppleMemberInfo(idToken)
        }

        return LoginResultDto(
            accessToken = "zz",
            refreshToken = "zz"
        )
    }

    fun getGoogleMemberInfo(idToken: String): String? {
        val verifier = GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory())
            .setAudience(listOf(env.getProperty("oauth2.google.client-id")))
            .build()
        val verifiedIdToken = verifier.verify(idToken)
        return verifiedIdToken?.payload?.email
    }

    fun getKakaoMemberInfo(providerIdToken: String): String {
        // TODO: Implement
        return ""
    }

    fun getAppleMemberInfo(providerIdToken: String): String {
        // TODO: Implement
        return ""
    }
}