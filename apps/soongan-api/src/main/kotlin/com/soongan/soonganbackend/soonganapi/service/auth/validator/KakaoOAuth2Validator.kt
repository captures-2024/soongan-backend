package com.soongan.soonganbackend.soonganapi.service.auth.validator

import com.fasterxml.jackson.annotation.JsonProperty
import com.soongan.soonganbackend.soonganapi.service.auth.validator.dto.OAuth2ValidateResult
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Service
class KakaoOAuth2Validator(
    private val restTemplate: RestTemplate
) {
    private val logger = KotlinLogging.logger {}

    fun validateTokenAndGetEmail(idToken: String): OAuth2ValidateResult {
        val startTime = System.currentTimeMillis()
        val url = "https://kapi.kakao.com/v2/user/me"

        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $idToken")
        }
        val request = HttpEntity<Unit>(headers)

        try {
            val response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                KakaoUserResponse::class.java
            )
            val duration = System.currentTimeMillis() - startTime

            val email = response.body?.kakaoAccount?.email
            if (email == null) {
                logger.warn { "[OAuth] kakao_validate_failed provider=KAKAO, duration=${duration}ms, reason=email not provided" }
                throw SoonganException(
                    StatusCode.INVALID_OAUTH2_ID_TOKEN,
                    "카카오 계정에 이메일 정보가 없습니다."
                )
            }

            return OAuth2ValidateResult(
                providerId = response.body?.id.toString(),
                email = email
            )
        } catch (e: HttpClientErrorException) {
            val duration = System.currentTimeMillis() - startTime
            logger.warn { "[OAuth] kakao_api_failed provider=KAKAO, statusCode=${e.statusCode.value()}, duration=${duration}ms, reason=invalid token or unauthorized" }
            throw SoonganException(
                StatusCode.INVALID_OAUTH2_ID_TOKEN,
                "카카오 API 호출 중 오류가 발생했습니다: ${e.message}"
            )
        } catch (e: HttpServerErrorException) {
            val duration = System.currentTimeMillis() - startTime
            logger.error(e) { "[OAuth] kakao_api_error provider=KAKAO, statusCode=${e.statusCode.value()}, duration=${duration}ms, reason=kakao server error" }
            throw SoonganException(
                StatusCode.INVALID_OAUTH2_ID_TOKEN,
                "카카오 API 호출 중 오류가 발생했습니다: ${e.message}"
            )
        } catch (e: SoonganException) {
            throw e
        } catch (e: RestClientException) {
            val duration = System.currentTimeMillis() - startTime
            logger.error(e) { "[OAuth] kakao_network_failed provider=KAKAO, duration=${duration}ms, reason=network error" }
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