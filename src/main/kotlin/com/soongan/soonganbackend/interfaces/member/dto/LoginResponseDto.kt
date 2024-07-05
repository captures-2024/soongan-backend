package com.soongan.soonganbackend.interfaces.member.dto

data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String
)
