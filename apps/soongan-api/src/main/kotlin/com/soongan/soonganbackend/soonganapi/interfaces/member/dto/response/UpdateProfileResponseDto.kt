package com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateProfileResponseDto(
    val newNickname: String? = null,
    val newSelfIntroduction: String? = null,
    val newProfileImageUrl: String? = null
)