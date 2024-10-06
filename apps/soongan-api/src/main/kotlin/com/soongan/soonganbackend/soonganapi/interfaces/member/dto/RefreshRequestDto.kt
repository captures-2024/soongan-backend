package com.soongan.soonganbackend.soonganapi.interfaces.member.dto

import jakarta.validation.constraints.NotNull

data class RefreshRequestDto(
    @field:NotNull(message = "accessToken 정보는 필수입니다.")
    val accessToken: String,
    @field:NotNull(message = "refreshToken 정보는 필수입니다.")
    val refreshToken: String
)
