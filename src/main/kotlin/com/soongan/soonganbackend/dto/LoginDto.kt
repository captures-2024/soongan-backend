package com.soongan.soonganbackend.dto

data class LoginDto(
    val provider: String,
    val accessToken: String
)