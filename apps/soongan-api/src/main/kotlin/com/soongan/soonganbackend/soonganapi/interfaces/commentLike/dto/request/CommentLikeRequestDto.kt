package com.soongan.soonganbackend.soonganapi.interfaces.commentLike.dto.request

import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(description = "댓글 좋아요 요청 DTO")
data class CommentLikeRequestDto (
    @Schema(description = "게시글 ID", required = true)
    @field:NotNull
    val commentId: Long,

    @Schema(description = "대회 타입 (weekly/daily)", required = true)
    @field:NotNull
    val contestTypeEnum: ContestTypeEnum
)
