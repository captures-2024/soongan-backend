package com.soongan.soonganbackend.soonganapi.interfaces.member.dto

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String
)
