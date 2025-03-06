package com.soongan.soonganbackend.soonganapi.interfaces.commentLike.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "댓글 좋아요 응답 DTO")
data class CommentLikeResponseDto(
    @Schema(description = "댓글 ID", required = true)
    val commentId: Long,

    @Schema(description = "동작 후 좋아요 수", required = true)
    val likeCount: Int
)
