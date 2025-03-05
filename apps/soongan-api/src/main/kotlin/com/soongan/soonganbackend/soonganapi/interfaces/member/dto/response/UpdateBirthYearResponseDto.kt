package com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "출생년도 수정 응답 DTO")
data class UpdateBirthYearResponseDto(
    @Schema(description = "출생년도", required = true)
    val birthYear: Int
)