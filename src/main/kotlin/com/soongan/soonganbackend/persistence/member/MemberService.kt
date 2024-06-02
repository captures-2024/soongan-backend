package com.soongan.soonganbackend.persistence.member

import com.google.gson.Gson
import com.soongan.soonganbackend.dto.LoginDto
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

        val userInfo = when (provider) {
            "google" -> getGoogleUserInfo(accessToken)
            else -> throw IllegalArgumentException("지원하지 않는 로그인 제공자입니다: $provider")
        }
        println(userInfo)

        return "provider: $provider, accessToken: $accessToken"
    }

    fun getGoogleUserInfo(accessToken: String): String {
        val url = "https://www.googleapis.com/oauth2/v2/userinfo"

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

        if (userInfo["error"] != null) {
            throw Exception("구글 사용자 정보를 가져오는데 실패했습니다: ${userInfo["error"]}")
        }

        return userInfo["email"].toString()
    }
}