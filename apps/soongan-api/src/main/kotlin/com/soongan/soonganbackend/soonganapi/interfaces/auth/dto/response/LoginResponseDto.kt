package com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "로그인 응답 DTO")
data class LoginResponseDto(
    @Schema(description = "새로 발급된 accessToken")
    val accessToken: String,

    @Schema(description = "새로 발급된 refreshToken")
    val refreshToken: String
)
