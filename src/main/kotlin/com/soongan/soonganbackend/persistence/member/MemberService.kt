package com.soongan.soonganbackend.persistence.member

import com.google.gson.Gson
import com.soongan.soonganbackend.dto.LoginDto
import com.soongan.soonganbackend.enums.Provider
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {

    fun login(loginDto: LoginDto): String {
        val provider = loginDto.provider
        val accessToken = loginDto.accessToken

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
        val userInfo = gson.fromJson(
            response.body?.string(),
            Map::class.java
        )
        val userEmail = when (provider) {
            Provider.GOOGLE -> userInfo["email"]
            Provider.KAKAO -> {
                val kakaoAccount = userInfo["kakao_account"] as Map<*, *>
                kakaoAccount["email"]
            }
            Provider.APPLE -> userInfo["email"]
        }

        val member = memberRepository.findByEmail(userEmail as String)
        if (member == null) {
            memberRepository.save(MemberEntity(email = userEmail, authorities = "Member", provider = provider))
        }

        return "provider: $provider, accessToken: $accessToken"
    }
}