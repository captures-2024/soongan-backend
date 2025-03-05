package com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(name = "RefreshRequest", description = "토큰 재발급 요청")
data class RefreshRequestDto(
    @Schema(description = "만료된 accessToken", required = true)
    @field:NotBlank(message = "accessToken 정보는 필수입니다.")
    val accessToken: String,

    @Schema(description = "재발급에 사용될 refreshToken", required = true)
    @field:NotBlank(message = "refreshToken 정보는 필수입니다.")
    val refreshToken: String
)
