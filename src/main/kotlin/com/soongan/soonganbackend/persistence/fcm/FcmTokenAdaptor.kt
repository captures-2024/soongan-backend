package com.soongan.soonganbackend.persistence.fcm

import com.soongan.soonganbackend.enums.UserAgent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FcmTokenAdaptor(
    private val fcmTokenRepository: FcmTokenRepository
) {

    @Transactional
    fun save(deviceType: UserAgent, token: String, deviceId: String): FcmTokenEntity {
        return fcmTokenRepository.save(
            FcmTokenEntity(
                deviceType = deviceType,
                token = token,
                deviceId = deviceId
            )
        )
    }
}