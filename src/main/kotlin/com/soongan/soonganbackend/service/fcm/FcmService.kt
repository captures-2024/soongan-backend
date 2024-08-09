package com.soongan.soonganbackend.service.fcm

import com.soongan.soonganbackend.enums.UserAgent
import com.soongan.soonganbackend.interfaces.fcm.dto.FcmRegistRequestDto
import com.soongan.soonganbackend.persistence.fcm.FcmTokenAdaptor
import com.soongan.soonganbackend.persistence.fcm.FcmTokenEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmService(
    private val fcmTokenAdaptor: FcmTokenAdaptor
) {

    @Transactional
    fun registFcmToken(userAgent: UserAgent, fcmRegistRequestDto: FcmRegistRequestDto): FcmTokenEntity {
        val savedFcmToken = fcmTokenAdaptor.save(
            deviceType = userAgent,
            token = fcmRegistRequestDto.token,
            deviceId = fcmRegistRequestDto.deviceId
        )

        return savedFcmToken
    }
}