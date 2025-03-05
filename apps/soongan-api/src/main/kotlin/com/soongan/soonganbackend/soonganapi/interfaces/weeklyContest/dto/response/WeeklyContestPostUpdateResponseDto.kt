package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "주간 대회 게시글 수정 응답 DTO")
data class WeeklyContestPostUpdateResponseDto(
    @Schema(description = "수정된 게시글 제목", required = true)
    val title: String
)