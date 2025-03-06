package com.soongan.soonganbackend.soonganapi.interfaces.postLike.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "게시글 좋아요 응답 DTO")
data class PostLikeResponseDto(
    @Schema(description = "게시글 ID", required = true)
    val postId: Long,

    @Schema(description = "동작 후 좋아요 수", required = true)
    val likeCount: Int
)
