package com.soongan.soonganbackend.soonganpersistence.storage.fcm

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FcmTokenAdapter(
    private val fcmTokenRepository: FcmTokenRepository
) {

    @Transactional
    fun save(fcmTokenEntity: FcmTokenEntity): FcmTokenEntity {
        return fcmTokenRepository.save(fcmTokenEntity)
    }

    @Transactional(readOnly = true)
    fun findAllByMemberId(memberId: Long): List<FcmTokenEntity> {
        return fcmTokenRepository.findByMember_Id(memberId)
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
