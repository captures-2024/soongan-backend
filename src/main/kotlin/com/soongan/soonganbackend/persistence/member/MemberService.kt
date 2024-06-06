package com.soongan.soonganbackend.persistence.member

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.soongan.soonganbackend.dto.LoginDto
import com.soongan.soonganbackend.dto.LoginResultDto
import com.soongan.soonganbackend.enums.Provider
import com.soongan.soonganbackend.exception.token.InvalidOAuth2IdTokenException
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
        val verifiedIdToken = verifier.verify(idToken)
        val email = verifiedIdToken.payload.email ?: InvalidOAuth2IdTokenException("Google IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다.")
        return email as String

    }

    fun getKakaoMemberEmail(providerIdToken: String): String {
        // TODO: Implement
        return ""
    }

    fun getAppleMemberEmail(providerIdToken: String): String {
        // TODO: Implement
        return ""
    }
}