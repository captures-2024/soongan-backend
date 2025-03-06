package com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "프로필 수정 응답 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateProfileResponseDto(
    @Schema(description = "닉네임")
    val nickname: String? = null,

    @Schema(description = "자기소개")
    val selfIntroduction: String? = null,

    @Schema(description = "프로필 이미지 URL")
    val profileImageUrl: String? = null
)