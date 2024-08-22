package com.soongan.soonganbackend.persistence.fcm

import org.springframework.data.jpa.repository.JpaRepository

interface FcmTokenRepository: JpaRepository<FcmTokenEntity, Long> {

    fun findByToken(token: String): FcmTokenEntity?
    fun findByDeviceId(deviceId: String): FcmTokenEntity?
}