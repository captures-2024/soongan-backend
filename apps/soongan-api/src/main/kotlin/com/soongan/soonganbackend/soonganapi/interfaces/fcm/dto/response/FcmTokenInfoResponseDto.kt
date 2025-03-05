package com.soongan.soonganbackend.soonganapi.interfaces.fcm.dto.response

import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "FCM 토큰 정보 응답 DTO")
data class FcmTokenInfoResponseDto(
    @Schema(description = "FCM 토큰 ID", required = true)
    val id: Long,

    @Schema(description = "FCM 토큰", required = true)
    val token: String,

    @Schema(description = "디바이스 ID", required = true)
    val deviceId: String,

    @Schema(description = "디바이스 타입", required = true)
    val deviceType: UserAgentEnum
)
