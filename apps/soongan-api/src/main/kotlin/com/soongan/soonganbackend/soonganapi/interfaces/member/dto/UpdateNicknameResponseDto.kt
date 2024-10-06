package com.soongan.soonganbackend.soonganapi.interfaces.member.dto

data class UpdateNicknameResponseDto(
    val memberEmail: String,
    val updatedNickname: String
)
