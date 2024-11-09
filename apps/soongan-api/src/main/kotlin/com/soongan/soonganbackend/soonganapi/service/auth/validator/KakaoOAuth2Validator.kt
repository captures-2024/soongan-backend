package com.soongan.soonganbackend.soonganapi.service.auth.validator

import com.fasterxml.jackson.annotation.JsonProperty
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class KakaoOAuth2Validator(
    private val restTemplate: RestTemplate
) {

    fun validateTokenAndGetEmail(idToken: String): String {
        val url = "https://kapi.kakao.com/v2/user/me"

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $idToken")
        }
        val request = HttpEntity<Unit>(headers)
        return try {
            val response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                KakaoUserResponse::class.java
            )

            response.body?.kakaoAccount?.email
                ?: throw SoonganException(
                    StatusCode.INVALID_OAUTH2_ID_TOKEN,
                    "카카오 이메일 정보를 찾을 수 없습니다."
                )
        } catch (e: RestClientException) {
            throw SoonganException(
                StatusCode.INVALID_OAUTH2_ID_TOKEN,
                "카카오 API 호출 중 오류가 발생했습니다: ${e.message}"
            )
        }
    }
}

data class KakaoUserResponse(
    val id: Long,
    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount?
)

data class KakaoAccount(
    val email: String?,
    @JsonProperty("email_verified")
    val emailVerified: Boolean?,
    @JsonProperty("has_email")
    val hasEmail: Boolean?
)