package com.soongan.soonganbackend.interfaces.member.dto

data class LoginResultDto(
    val accessToken: String,
    val refreshToken: String
)
