package com.soongan.soonganbackend.soonganpersistence.storage.notification

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository: JpaRepository<NotificationEntity, Long> {

    fun findByMemberId(memberId: Long): List<NotificationEntity>
}