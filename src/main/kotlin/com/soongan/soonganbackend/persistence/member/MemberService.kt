package com.soongan.soonganbackend.persistence.member

import com.google.gson.Gson
import com.soongan.soonganbackend.dto.LoginDto
import com.soongan.soonganbackend.dto.LoginResultDto
import com.soongan.soonganbackend.enums.Provider
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val jwtService: JwtService
) {

    fun login(loginDto: LoginDto): LoginResultDto {
        val provider = loginDto.provider
        val accessToken = loginDto.accessToken

        val userInfo = getOAuth2UserInfo(provider, accessToken)
        println(userInfo)
        if (userInfo["error"] != null) {
            throw Exception("유효하지 않은 토큰으로 인해 회원 정보를 가져올 수 없습니다.")
        }

        val userEmail = when (provider) {
            Provider.GOOGLE -> userInfo["email"]
            Provider.KAKAO -> {
                val kakaoAccount = userInfo["kakao_account"] as Map<*, *>
                kakaoAccount["email"]
            }
            Provider.APPLE -> userInfo["email"]
        }

        val member = memberRepository.findByEmail(userEmail as String)
            ?: memberRepository.save(MemberEntity(email = userEmail, authorities = "Member", provider = provider))

        val tokens = jwtService.issueTokens(member.email, member.authorities.split(","))
        return LoginResultDto(
            accessToken = tokens.first,
            refreshToken = tokens.second
        )
    }

    fun getOAuth2UserInfo(provider: Provider, accessToken: String): Map<*, *> {
        val url = when (provider) {
            Provider.GOOGLE -> "https://www.googleapis.com/oauth2/v2/userinfo"
            Provider.KAKAO -> "https://kapi.kakao.com/v2/user/me"
            Provider.APPLE -> "https://appleid.apple.com/auth/user"
        }

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $accessToken")
            .build()
        val response = client.newCall(request).execute()

        val gson = Gson()
        return gson.fromJson(
            response.body?.string(),
            Map::class.java
        )
    }
}