package com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response

import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import jakarta.validation.constraints.Min

data class GetNotificationCountResponseDto (
    @field:Min(0)
    val count: Int,
    val type: NotificationTypeEnum
)
