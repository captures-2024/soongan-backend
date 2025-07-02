package com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "알림 설정 업데이트 요청 DTO")
data class UpdateNotiSettingRequestDto(
    @Schema(description = "대회 알림 설정", required = false)
    val contestPush: Boolean?,

    @Schema(description = "활동 알림 설정", required = false)
    val activityPush: Boolean?,

    @Schema(description = "공지 알림 설정", required = false)
    val noticePush: Boolean?
)
