package com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateProfileResponseDto(
    val nickname: String? = null,
    val selfIntroduction: String? = null,
    val profileImageUrl: String? = null
)