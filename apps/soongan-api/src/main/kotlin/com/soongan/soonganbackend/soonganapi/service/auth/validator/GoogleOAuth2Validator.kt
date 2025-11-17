package com.soongan.soonganbackend.soonganapi.service.auth.validator

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.soongan.soonganbackend.soonganapi.service.auth.validator.dto.OAuth2ValidateResult
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class GoogleOAuth2Validator(
    private val env: Environment
) {
    private val logger = KotlinLogging.logger {}

    fun validateTokenAndGetEmail(idToken: String, userAgent: UserAgentEnum): OAuth2ValidateResult {
        val startTime = System.currentTimeMillis()
        val clientId = when (userAgent) {
            UserAgentEnum.ANDROID -> env.getProperty("oauth2.android.google.client-id")
            UserAgentEnum.IOS -> env.getProperty("oauth2.ios.google.client-id")
        }
        val verifier = GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory())
            .setAudience(listOf(clientId))
            .build()

        try {
            val verifiedIdToken = verifier.verify(idToken)
            val duration = System.currentTimeMillis() - startTime

            if (verifiedIdToken == null) {
                logger.warn { "[OAuth] google_validate_failed provider=GOOGLE, userAgent=$userAgent, duration=${duration}ms, reason=invalid token" }
                throw SoonganException(
                    StatusCode.INVALID_OAUTH2_ID_TOKEN,
                    "Google IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다."
                )
            }

            return OAuth2ValidateResult(
                providerId = verifiedIdToken.payload.subject,
                email = verifiedIdToken.payload.email
            )
        } catch (e: SoonganException) {
            throw e
        } catch (e: IllegalArgumentException) {
            val duration = System.currentTimeMillis() - startTime
            logger.warn { "[OAuth] google_validate_failed provider=GOOGLE, userAgent=$userAgent, duration=${duration}ms, reason=malformed token format" }
            throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "잘못된 Google IdToken 형식으로 회원 정보를 가져올 수 없습니다.")
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            logger.error(e) { "[OAuth] google_validate_error provider=GOOGLE, userAgent=$userAgent, duration=${duration}ms, reason=network or system error" }
            throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "Google 인증 중 오류가 발생했습니다: ${e.message}")
        }
    }
}