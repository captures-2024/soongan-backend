package com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.response

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String
)
