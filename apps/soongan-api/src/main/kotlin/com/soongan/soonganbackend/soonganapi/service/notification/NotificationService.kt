package com.soongan.soonganbackend.soonganapi.service.notification

import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.GetNotificationCountResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.GetNotificationResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.notification.NotificationAdapter
import com.soongan.soonganbackend.soongansupport.domain.NotificationSubTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
    private val notificationAdapter: NotificationAdapter
) {

    fun countNotification(loginMember: MemberEntity): List<GetNotificationCountResponseDto> {
        return notificationAdapter.countNotification(loginMember).map {
            GetNotificationCountResponseDto(it.getNotificationCount().toInt(), it.getType())
        }
    }

    fun getNotifications(loginMember: MemberEntity, type: NotificationTypeEnum): GetNotificationResponseDto {
        val notifications = notificationAdapter.getNotificationByType(loginMember, type)
        return GetNotificationResponseDto.from(type, notifications)
    }

    fun readNotification(notificationId: Long): Long {
        val notification = notificationAdapter.getByIdOrNull(notificationId)
            ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_NOTIFICATION)

        if (notification.isRead) {
            throw SoonganException(StatusCode.SOONGAN_API_ALREADY_READ_NOTIFICATION)
        }

        val savedEntity = notificationAdapter.save(notification.copy(isRead = true))

        return savedEntity.id!!
    }

    @Transactional
    fun deleteNotification(notificationId: Long) {
        val notification = notificationAdapter.getByIdOrNull(notificationId)
            ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_NOTIFICATION)

        if (notification.subType == NotificationSubTypeEnum.APPEAL) {
            throw SoonganException(StatusCode.SOONGAN_API_CANNOT_DELETE_REPORT_CLARIFICATION)
        }

        notificationAdapter.delete(notification)
    }
}
