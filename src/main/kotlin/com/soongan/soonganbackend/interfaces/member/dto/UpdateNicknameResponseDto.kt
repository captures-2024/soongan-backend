package com.soongan.soonganbackend.interfaces.member.dto

data class UpdateNicknameResponseDto(
    val memberEmail: String,
    val updatedNickname: String
)