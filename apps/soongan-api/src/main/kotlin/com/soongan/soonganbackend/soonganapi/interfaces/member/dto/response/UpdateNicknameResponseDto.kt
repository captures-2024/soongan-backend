package com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response

data class UpdateNicknameResponseDto(
    val memberEmail: String,
    val updatedNickname: String
)
