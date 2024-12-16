package com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.notification.NotificationEntity
import com.soongan.soonganbackend.soongansupport.domain.NotificationSubTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import java.time.LocalDateTime

data class GetNotificationResponseDto(
    val type: NotificationTypeEnum,
    val notifications: List<NotificationDto>
) {
    companion object {
        fun from(type: NotificationTypeEnum, notifications: List<NotificationEntity>): GetNotificationResponseDto {
            return GetNotificationResponseDto(
                type = type,
                notifications = notifications.map {
                    NotificationDto(
                        id = it.id!!,
                        title = it.title,
                        content = it.content,
                        subType = it.subType,
                        isRead = it.isRead,
                        redirectUrl = it.redirectUrl,
                        createdAt = it.createdAt
                    )
                }
            )
        }
    }
}

data class NotificationDto(
    val id: Long,
    val title: String,
    val content: String,
    val subType: NotificationSubTypeEnum,
    val isRead: Boolean,
    val redirectUrl: String? = null,
    val createdAt: LocalDateTime
)


