package com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.notification.NotificationEntity
import com.soongan.soonganbackend.soongansupport.domain.NotificationSubTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "알림 조회 응답 DTO")
data class GetNotificationResponseDto(
    @Schema(description = "알림 타입", required = true)
    val type: NotificationTypeEnum,

    @Schema(description = "알림 목록", required = true)
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
                        body = it.body,
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
    val body: String,
    val subType: NotificationSubTypeEnum,
    val isRead: Boolean,
    val redirectUrl: String? = null,
    val createdAt: LocalDateTime
)


