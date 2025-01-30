package com.soongan.soonganbackend.soonganapi.service.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.soongan.soonganbackend.soonganapi.interfaces.fcm.dto.request.FcmRegistRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.fcm.dto.response.FcmTokenInfoResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenEntity
import com.soongan.soonganbackend.soonganredis.constant.RedisStreamKey
import com.soongan.soonganbackend.soonganredis.producer.RedisMessageProducer
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.dto.FcmMessageDto
import com.soongan.soonganbackend.soongansupport.util.dto.Message
import com.soongan.soonganbackend.soongansupport.util.dto.MessageData
import com.soongan.soonganbackend.soongansupport.util.dto.Notification
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@Service
class FcmService(
    private val fcmTokenAdapter: FcmTokenAdapter,
    private val redisMessageProducer: RedisMessageProducer,
    private val restTemplate: RestTemplate
) {

    @Value("\${firebase.project-id}")
    private lateinit var firebaseProjectId: String

    @Value("\${firebase.key-json-string}")
    private lateinit var firebaseKeyJsonString: String

    @Transactional
    fun registFcmToken(userAgentEnum: UserAgentEnum, fcmRegistRequestDto: FcmRegistRequestDto): FcmTokenInfoResponseDto {
        fcmTokenAdapter.findByToken(token = fcmRegistRequestDto.token)?.let {
            throw SoonganException(StatusCode.SOONGAN_API_ALREADY_EXIST_FCM_TOKEN)
        }

        val foundFcmTokenByDeviceId = fcmTokenAdapter.findByDeviceId(deviceId = fcmRegistRequestDto.deviceId)
        if (foundFcmTokenByDeviceId != null) {
            val updatedFcmToken = fcmTokenAdapter.save(
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

        val savedFcmToken = fcmTokenAdapter.save(
            FcmTokenEntity(
                deviceType = userAgentEnum,
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

    fun testFcmPush(fcmToken: String) {
        val message = Message(
            tokens = listOf(fcmToken),
            notification = Notification(
                title = "테스트 알림",
                body = "테스트 알림입니다."
            ),
            data = MessageData(
                link = "https://api-dev.soongan.com/api-docs",
                notificationType = NotificationTypeEnum.ACTIVITY
            )
        )
        redisMessageProducer.sendMessage(RedisStreamKey.SOONGAN_NOTI, message)
    }
}
