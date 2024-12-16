package com.soongan.soonganbackend.soonganapi.service.notification

import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.GetNotificationCountResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.GetNotificationResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.notification.NotificationAdaptor
import com.soongan.soonganbackend.soonganpersistence.storage.notification.NotificationCountSummary
import com.soongan.soonganbackend.soongansupport.domain.NotificationSubTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
    private val notificationAdaptor: NotificationAdaptor
) {

    fun countNotification(loginMember: MemberEntity): List<GetNotificationCountResponseDto> {
        return notificationAdaptor.countNotification(loginMember).map {
            GetNotificationCountResponseDto(it.getNotificationCount().toInt(), it.getType())
        }
    }

    fun getNotification(loginMember: MemberEntity, type: NotificationTypeEnum): GetNotificationResponseDto {
        val notifications = notificationAdaptor.getNotificationByType(loginMember, type)
        return GetNotificationResponseDto.from(type, notifications)
    }

    fun readNotification(notificationId: Long): Long {
        val notification = notificationAdaptor.getByIdOrNull(notificationId)
            ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_NOTIFICATION)

        if (notification.isRead) {
            throw SoonganException(StatusCode.SOONGAN_API_ALREADY_READ_NOTIFICATION)
        }

        val savedEntity = notificationAdaptor.save(notification.copy(isRead = true))

        return savedEntity.id!!
    }

    @Transactional
    fun deleteNotification(notificationId: Long) {
        val notification = notificationAdaptor.getByIdOrNull(notificationId)
            ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_NOTIFICATION)

        if (notification.subType == NotificationSubTypeEnum.APPEAL) {
            throw SoonganException(StatusCode.SOONGAN_API_CANNOT_DELETE_REPORT_CLARIFICATION)
        }

        notificationAdaptor.delete(notification)
    }
}
