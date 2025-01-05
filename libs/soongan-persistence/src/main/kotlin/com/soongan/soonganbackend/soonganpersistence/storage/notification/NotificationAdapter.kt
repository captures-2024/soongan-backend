package com.soongan.soonganbackend.soonganpersistence.storage.notification

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationAdapter(
    private val notificationRepository: NotificationRepository
) {

    @Transactional
    fun save(notificationEntity: NotificationEntity): NotificationEntity {
        return notificationRepository.save(notificationEntity)
    }

    @Transactional(readOnly = true)
    fun getByIdOrNull(id: Long): NotificationEntity? {
        return notificationRepository.findByIdOrNull(id)
    }

    @Transactional(readOnly = true)
    fun countNotification(member: MemberEntity): List<NotificationCountSummary> {
        return notificationRepository.countUnreadNotifications(member)
    }

    @Transactional(readOnly = true)
    fun getNotificationByType(member: MemberEntity, type: NotificationTypeEnum): List<NotificationEntity> {
        return notificationRepository.findAllByMemberAndType(member, type)
    }

    @Transactional
    fun delete(notificationEntity: NotificationEntity) {
        notificationRepository.delete(notificationEntity)
    }
}
