package com.soongan.soonganbackend.persistence.fcm

import com.soongan.soonganbackend.enums.UserAgent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FcmTokenAdaptor(
    private val fcmTokenRepository: FcmTokenRepository
) {

    @Transactional
    fun save(fcmTokenEntity: FcmTokenEntity): FcmTokenEntity {
        return fcmTokenRepository.save(fcmTokenEntity)
    }

    @Transactional(readOnly = true)
    fun findByToken(token: String): FcmTokenEntity? {
        return fcmTokenRepository.findByToken(token)
    }

    @Transactional(readOnly = true)
    fun findByDeviceId(deviceId: String): FcmTokenEntity? {
        return fcmTokenRepository.findByDeviceId(deviceId)
    }
}