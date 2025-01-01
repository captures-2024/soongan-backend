package com.soongan.soonganbackend.soonganapi.unit.service.fcm

import com.fasterxml.jackson.databind.ObjectMapper
import com.soongan.soonganbackend.soonganapi.interfaces.fcm.dto.request.FcmRegistRequestDto
import com.soongan.soonganbackend.soonganapi.service.fcm.FcmService
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenEntity
import com.soongan.soonganbackend.soonganredis.producer.RedisMessageProducer
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.client.RestTemplate

@ExtendWith(MockKExtension::class)
class FcmServiceTest {

    @MockK
    private lateinit var fcmTokenAdapter: FcmTokenAdapter

    @MockK
    private lateinit var redisMessageProducer: RedisMessageProducer

    @MockK
    private lateinit var objectMapper: ObjectMapper

    @MockK
    private lateinit var restTemplate: RestTemplate

    @InjectMockKs
    private lateinit var fcmService: FcmService

    @BeforeEach
    fun setUp() {
        fcmTokenAdapter = mockk()
        redisMessageProducer = mockk()
        objectMapper = mockk()
        restTemplate = mockk()
        fcmService = FcmService(fcmTokenAdapter, redisMessageProducer, objectMapper, restTemplate)
    }

    @Test
    fun `fcm 토큰 등록 성공 - 같은 device id로 등록된 토큰이 없는 경우`() {
        // given
        val userAgentEnum = UserAgentEnum.ANDROID
        val request = FcmRegistRequestDto(
            token = "test-token",
            deviceId = "test-device-id"
        )
        val fcmToken = FcmTokenEntity(
            token = request.token,
            deviceId = request.deviceId,
            deviceType = userAgentEnum
        )

        // mock
        every { fcmTokenAdapter.findByToken(token = request.token) } returns null
        every { fcmTokenAdapter.findByDeviceId(deviceId = request.deviceId) } returns null
        every { fcmTokenAdapter.save(any()) } returns fcmToken.copy(id = 1)

        // when
        val result = fcmService.registFcmToken(userAgentEnum, request)

        // then
        assertThat(result.id).isNotNull()
        assertThat(result.token).isEqualTo(request.token)
        assertThat(result.deviceId).isEqualTo(request.deviceId)
        assertThat(result.deviceType).isEqualTo(userAgentEnum)
    }

    @Test
    fun `fcm 토큰 등록 성공 - 같은 device id로 등록된 토큰이 있는 경우`() {
        // given
        val userAgentEnum = UserAgentEnum.ANDROID
        val request = FcmRegistRequestDto(
            token = "test-token",
            deviceId = "test-device-id"
        )
        val fcmToken = FcmTokenEntity(
            id = 1,
            token = "exist-token",
            deviceId = request.deviceId,
            deviceType = userAgentEnum
        )

        // mock
        every { fcmTokenAdapter.findByToken(token = request.token) } returns null
        every { fcmTokenAdapter.findByDeviceId(deviceId = request.deviceId) } returns fcmToken
        every { fcmTokenAdapter.save(any()) } returns fcmToken.copy(token = request.token)

        // when
        val result = fcmService.registFcmToken(userAgentEnum, request)

        // then
        assertThat(result.id).isNotNull()
        assertThat(result.token).isEqualTo(request.token)
        assertThat(result.deviceId).isEqualTo(request.deviceId)
        assertThat(result.deviceType).isEqualTo(userAgentEnum)
    }

    @Test
    fun `fcm 토큰 등록 실패 - 이미 등록된 토큰인 경우`() {
        // given
        val userAgentEnum = UserAgentEnum.ANDROID
        val request = FcmRegistRequestDto(
            token = "test-token",
            deviceId = "test-device-id"
        )

        // mock
        every { fcmTokenAdapter.findByToken(token = request.token) } returns FcmTokenEntity(
            token = request.token,
            deviceId = "exist-device-id",
            deviceType = userAgentEnum
        )

        // when, then
        val exception = assertThrows<SoonganException> {
            fcmService.registFcmToken(userAgentEnum, request)
        }
        assertThat(exception.statusCode).isEqualTo(StatusCode.SOONGAN_API_ALREADY_EXIST_FCM_TOKEN)
    }

    // TODO: FCM 메시지 전송 메서드 테스트 코드 작성
}