package com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response

import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "알림 개수 조회 응답 DTO")
data class GetNotificationCountResponseDto (
    @Schema(description = "알림 개수", required = true)
    val count: Int,

    @Schema(description = "알림 타입", required = true)
    val type: NotificationTypeEnum
)
