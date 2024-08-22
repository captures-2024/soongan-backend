package com.soongan.soonganbackend.service.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.soongan.soonganbackend.enums.UserAgent
import com.soongan.soonganbackend.interfaces.fcm.dto.*
import com.soongan.soonganbackend.persistence.fcm.FcmTokenAdaptor
import com.soongan.soonganbackend.persistence.fcm.FcmTokenEntity
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@Service
class FcmService(
    private val fcmTokenAdaptor: FcmTokenAdaptor,
    private val restTemplate: RestTemplate,
    private val env: Environment
) {

    @Transactional
    fun registFcmToken(userAgent: UserAgent, fcmRegistRequestDto: FcmRegistRequestDto): FcmTokenInfoResponseDto {
        fcmTokenAdaptor.findByToken(token = fcmRegistRequestDto.token)?.let {
            throw SoonganException(StatusCode.SOONGAN_API_ALREADY_EXIST_FCM_TOKEN)
        }

        val foundFcmTokenByDeviceId = fcmTokenAdaptor.findByDeviceId(deviceId = fcmRegistRequestDto.deviceId)
        if (foundFcmTokenByDeviceId != null) {
            val updatedFcmToken = fcmTokenAdaptor.save(
                foundFcmTokenByDeviceId.copy(
                    token = fcmRegistRequestDto.token
                )
            )

            return FcmTokenInfoResponseDto(
                id = updatedFcmToken.id!!,
                token = updatedFcmToken.token,
                deviceId = updatedFcmToken.deviceId,
                deviceType = updatedFcmToken.deviceType
            )
        }

        val savedFcmToken = fcmTokenAdaptor.save(
            FcmTokenEntity(
                deviceType = userAgent,
                token = fcmRegistRequestDto.token,
                deviceId = fcmRegistRequestDto.deviceId
            )
        )

        return FcmTokenInfoResponseDto(
            id = savedFcmToken.id!!,
            token = savedFcmToken.token,
            deviceId = savedFcmToken.deviceId,
            deviceType = savedFcmToken.deviceType
        )
    }

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