package com.soongan.soonganbackend.soonganapi.interfaces.auth.response

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String
)
