package com.soongan.soonganbackend.soonganconsumer.service.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.soongan.soonganbackend.soongansupport.util.dto.FcmMessageDto
import com.soongan.soonganbackend.soongansupport.util.dto.Message
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class FcmService(
    private val restTemplate: RestTemplate
) {

    @Value("\${firebase.project-id}")
    private lateinit var firebaseProjectId: String

    @Value("\${firebase.key-json-string}")
    private lateinit var firebaseKeyJsonString: String

    fun pushFcmMessage(message: Message): ResponseEntity<Map<String, Any>> {
        val url = "https://fcm.googleapis.com/v1/projects/${firebaseProjectId}/messages:send"

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
        val googleCredentials = GoogleCredentials.fromStream(firebaseKeyJsonString.byteInputStream())
            .createScoped("https://www.googleapis.com/auth/cloud-platform")
        googleCredentials.refreshIfExpired()
        return googleCredentials.accessToken.tokenValue
    }
}