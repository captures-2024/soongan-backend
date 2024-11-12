package com.soongan.soonganbackend.soonganconsumer.service.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.soongan.soonganbackend.soonganconsumer.service.fcm.dto.FcmMessageDto
import com.soongan.soonganbackend.soonganconsumer.service.fcm.dto.Message
import com.soongan.soonganbackend.soonganconsumer.service.fcm.dto.Notification
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class FcmService(
    private val restTemplate: RestTemplate,
    private val env: Environment
) {

    fun pushFcmMessage(fcmToken: String, notification: Notification): ResponseEntity<Map<String, Any>> {
        val firebaseProjectId = env.getProperty("firebase.project-id").toString()
        val url = "https://fcm.googleapis.com/v1/projects/${firebaseProjectId}/messages:send"
        val message = Message(
            token = fcmToken,
            notification = notification
        )

        val headers = HttpHeaders().apply {
            setBearerAuth(getFcmAccessToken())
            contentType = MediaType.APPLICATION_JSON
        }

        val fcmMessageDto = FcmMessageDto(message = message)
        val request = HttpEntity(fcmMessageDto, headers)

        val response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            object : ParameterizedTypeReference<Map<String, Any>>() {}
        )

        if (!response.statusCode.is2xxSuccessful) {
            throw SoonganException(StatusCode.SOONGAN_API_FAILED_PUSH_FCM_MESSAGE)
        }

        return response
    }

    fun getFcmAccessToken(): String {
        val firebaseKeyPath = env.getProperty("firebase.keypath").toString()
        val googleCredentials = GoogleCredentials.fromStream(ClassPathResource(firebaseKeyPath).inputStream)
            .createScoped("https://www.googleapis.com/auth/cloud-platform")
        googleCredentials.refreshIfExpired()
        return googleCredentials.accessToken.tokenValue
    }
}