package com.soongan.soonganbackend.persistence.member

import com.soongan.soonganbackend.dto.LoginDto
import com.soongan.soonganbackend.dto.LoginResultDto
import com.soongan.soonganbackend.enums.Provider
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val jwtService: JwtService
) {
    fun login(loginDto: LoginDto): LoginResultDto {
        val provider = loginDto.provider
        val providerIdToken = loginDto.providerIdToken

        val memberInfo = when (provider) {
            Provider.GOOGLE -> getGoogleMemberInfo(providerIdToken)
            Provider.KAKAO -> getKakaoMemberInfo(providerIdToken)
            Provider.APPLE -> getAppleMemberInfo(providerIdToken)
        }

        return LoginResultDto(
            accessToken = "zz",
            refreshToken = "zz"
        )
    }

    fun getGoogleMemberInfo(providerIdToken: String): String {
        // TODO: Implement
        return ""
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