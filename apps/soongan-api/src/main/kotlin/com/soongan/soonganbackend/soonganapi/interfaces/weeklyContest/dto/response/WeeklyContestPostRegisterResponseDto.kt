package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "주간 대회 게시글 등록 응답 DTO")
data class WeeklyContestPostRegisterResponseDto(
    @Schema(description = "게시글 ID", required = true)
    val postId: Long,

    @Schema(description = "게시글 제목", required = true)
    val title: String,

    @Schema(description = "게시글 청부 이미지 URL", required = true)
    val imageUrl: String,

    @Schema(description = "게시글 작성자 닉네임", required = true)
    val registerNickname: String
)
