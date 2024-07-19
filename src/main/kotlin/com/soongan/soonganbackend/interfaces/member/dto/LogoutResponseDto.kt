package com.soongan.soonganbackend.interfaces.member.dto

data class LogoutResponseDto(
    val memberEmail: String,
    val message: String
)