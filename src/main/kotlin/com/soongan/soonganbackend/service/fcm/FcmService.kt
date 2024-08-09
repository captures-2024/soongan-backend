package com.soongan.soonganbackend.service.fcm

import com.soongan.soonganbackend.enums.UserAgent
import com.soongan.soonganbackend.interfaces.fcm.dto.FcmRegistRequestDto
import com.soongan.soonganbackend.interfaces.fcm.dto.FcmTokenInfoResponseDto
import com.soongan.soonganbackend.persistence.fcm.FcmTokenAdaptor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmService(
    private val fcmTokenAdaptor: FcmTokenAdaptor
) {

    @Transactional
    fun registFcmToken(userAgent: UserAgent, fcmRegistRequestDto: FcmRegistRequestDto): FcmTokenInfoResponseDto {
        val savedFcmToken = fcmTokenAdaptor.save(
            deviceType = userAgent,
            token = fcmRegistRequestDto.token,
            deviceId = fcmRegistRequestDto.deviceId
        )

        return FcmTokenInfoResponseDto(
            id = savedFcmToken.id!!,
            token = savedFcmToken.token,
            deviceId = savedFcmToken.deviceId,
            deviceType = savedFcmToken.deviceType
        )
    }
}