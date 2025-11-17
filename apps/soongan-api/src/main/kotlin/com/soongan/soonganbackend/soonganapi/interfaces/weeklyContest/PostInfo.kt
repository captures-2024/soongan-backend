package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "게시글 정보")
data class PostInfo(
    @Schema(description = "주간 콘테스트 라운드", required = true)
    val round: Int,

    @Schema(description = "주간 콘테스트 주제", required = true)
    val subject: String,

    @Schema(description = "게시글 ID", required = true)
    val postId: Long,

    @Schema(description = "게시글 이미지 URL", required = true)
    val imageUrl: String,

    @Schema(description = "좋아요 수", required = true)
    val likeCount: Int,

    @Schema(description = "신고 수", required = true)
    val reportCount: Long,
)
