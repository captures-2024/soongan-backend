package com.soongan.soonganbackend.soonganapi.interfaces.fcm.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "FCM 토큰 등록 요청 DTO")
data class FcmRegistRequestDto(
    @Schema(description = "FCM 토큰", required = true)
    @field:NotBlank
    val token: String,

    @Schema(description = "디바이스 ID", required = true)
    @field:NotBlank
    val deviceId: String
)
