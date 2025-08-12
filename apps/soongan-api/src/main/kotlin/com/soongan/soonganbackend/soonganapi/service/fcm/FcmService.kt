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
import com.soongan.soonganbackend.soongansupport.util.dto.Notification
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException
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

    private val logger = KotlinLogging.logger { }

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

    fun pushFcmMessage(messages: List<Message>) {
        val url = "https://fcm.googleapis.com/v1/projects/$firebaseProjectId/messages:send"

        val headers = HttpHeaders().apply {
            setBearerAuth(getFcmAccessToken())
            contentType = MediaType.APPLICATION_JSON
        }

        for (msg in messages) {
            try {
                val req = HttpEntity(FcmMessageDto(message = msg), headers)
                val started = System.currentTimeMillis()

                val res = restTemplate.exchange(
                    url, HttpMethod.POST, req,
                    object : ParameterizedTypeReference<Map<String, Any>>() {}
                )

                val took = System.currentTimeMillis() - started
                val name = res.body?.get("name") as? String ?: ""
                logger.info {
                    "FCM 전송 성공 name=$name took=${took}ms token=${mask(msg.token)} " +
                            "type=${msg.data["notificationType"]} postId=${msg.data["postId"]}"
                }
            } catch (e: RestClientResponseException) {
                // 4xx/5xx 응답 (본문 포함)
                logger.error(e) {
                    "FCM 전송 실패 status=${e.statusCode} body=${e.responseBodyAsString} " +
                            "token=${mask(msg.token)}"
                }
            } catch (e: RestClientException) {
                // 네트워크/직렬화 등 전송 레벨 예외
                logger.error(e) { "FCM 전송 실패 transport=${e.message} token=${mask(msg.token)}" }
            }
        }
    }

    private fun mask(token: String) =
        if (token.length > 16) token.take(8) + "…" + token.takeLast(6) else "****"


    fun getFcmAccessToken(): String {
        val googleCredentials = GoogleCredentials.fromStream(firebaseKeyJsonString.byteInputStream())
            .createScoped("https://www.googleapis.com/auth/cloud-platform")
        googleCredentials.refreshIfExpired()
        return googleCredentials.accessToken.tokenValue
    }

    fun testFcmPush(fcmToken: String) {
        val message = Message(
            token = fcmToken,
            notification = Notification(
                title = "테스트 알림",
                body = "테스트 알림입니다."
            ),
            data = mapOf(
                "link" to "https://api-dev.soongan.com/api-docs",
                "notificationType" to NotificationTypeEnum.ACTIVITY.toString(),
            )
        )
        redisMessageProducer.addMessage(RedisStreamKey.SOONGAN_NOTI, listOf(message))
    }
}
