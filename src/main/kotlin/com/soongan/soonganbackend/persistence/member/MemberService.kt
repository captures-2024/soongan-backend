package com.soongan.soonganbackend.persistence.member

import com.google.gson.Gson
import com.soongan.soonganbackend.dto.LoginDto
import com.soongan.soonganbackend.dto.LoginResultDto
import com.soongan.soonganbackend.enums.Provider
import okhttp3.FormBody
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

    private val client = OkHttpClient()
    private val gson = Gson()

    fun login(loginDto: LoginDto): LoginResultDto {
        val provider = loginDto.provider
        val accessToken = loginDto.accessToken

        var userInfo = getOAuth2UserInfo(provider, accessToken)
        if (userInfo["error"] != null) {
            val newAccessToken = refreshOAuth2AccessToken(provider, accessToken)
            userInfo = getOAuth2UserInfo(provider, newAccessToken)
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

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $accessToken")
            .build()
        val response = client.newCall(request).execute()

        return gson.fromJson(
            response.body?.string(),
            Map::class.java
        )
    }

    fun refreshOAuth2AccessToken(provider: Provider, refreshToken: String): String {
        val url = when (provider) {
            Provider.GOOGLE -> "https://oauth2.googleapis.com/token"
            Provider.KAKAO -> "https://kauth.kakao.com/oauth/token"
            Provider.APPLE -> "https://appleid.apple.com/auth/token"
        }

        val reqBody = FormBody.Builder()
            .add("client_id", env.getProperty("oauth2.google.client-id")!!)
            .add("client_secret", env.getProperty("oauth2.google.client-secret")!!)
            .add("grant_type", "refresh_token")
            .add("refresh_token", refreshToken)
            .build()
        val request = Request.Builder()
            .url(url)
            .post(reqBody)
            .build()

        val response = client.newCall(request).execute()
        return gson.fromJson(
            response.body?.string(),
            Map::class.java
        )["access_token"] as String
    }
}