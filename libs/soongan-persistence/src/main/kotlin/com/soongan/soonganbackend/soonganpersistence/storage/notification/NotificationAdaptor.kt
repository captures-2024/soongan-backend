package com.soongan.soonganbackend.soonganpersistence.storage.notification

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationAdaptor(
    private val notificationRepository: NotificationRepository
) {

    @Transactional
    fun save(notificationEntity: NotificationEntity): NotificationEntity {
        return notificationRepository.save(notificationEntity)
    }

    @Transactional(readOnly = true)
    fun findByMemberId(memberId: Long): List<NotificationEntity> {
        return notificationRepository.findByMemberId(memberId)
    }
}