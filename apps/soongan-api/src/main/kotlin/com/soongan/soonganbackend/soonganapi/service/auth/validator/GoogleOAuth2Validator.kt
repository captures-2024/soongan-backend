package com.soongan.soonganbackend.soonganapi.service.auth.validator

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class GoogleOAuth2Validator(
    private val env: Environment
) {

    fun validateTokenAndGetEmail(idToken: String, userAgent: UserAgentEnum): String {
        val clientId = when (userAgent) {
            UserAgentEnum.ANDROID -> env.getProperty("oauth2.android.google.client-id")
            UserAgentEnum.IOS -> env.getProperty("oauth2.ios.google.client-id")
        }
        val verifier = GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory())
            .setAudience(listOf(clientId))
            .build()


        try {
            val verifiedIdToken = verifier.verify(idToken)
                ?: throw SoonganException(  // 토큰이 유효하지 않은 경우
                    StatusCode.INVALID_OAUTH2_ID_TOKEN,
                    "Google IdToken이 유효하지 않아 회원 정보를 가져올 수 없습니다."
                )

            val email = verifiedIdToken.payload.email
            return email as String
        } catch (e: IllegalArgumentException) {  // 토큰 자체 형식이 맞지 않아 해독 도중 에러가 발생한 경우
            throw SoonganException(StatusCode.INVALID_OAUTH2_ID_TOKEN, "잘못된 Google IdToken 형식으로 회원 정보를 가져올 수 없습니다.")
        }
    }
}