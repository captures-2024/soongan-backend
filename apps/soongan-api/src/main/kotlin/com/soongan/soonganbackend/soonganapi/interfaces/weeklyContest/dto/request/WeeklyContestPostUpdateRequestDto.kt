package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "주간 대회 게시글 수정 요청 DTO")
data class WeeklyContestPostUpdateRequestDto(
    @Schema(description = "게시글 제목", required = true)
    @field:NotBlank
    val title: String
)