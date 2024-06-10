package com.soongan.soonganbackend.dto

data class LoginResultDto(
    val accessToken: String,
    val refreshToken: String
)