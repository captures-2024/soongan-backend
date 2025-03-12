package com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request

import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "댓글 저장 요청 DTO")
data class CommentSaveRequestDto (
    @Schema(description = "대회 타입 (weekly/daily)", required = true)
    @field:NotNull
    val contestType: ContestTypeEnum,

    @Schema(description = "게시글 ID", required = true)
    @field:NotNull
    val postId: Long,

    @Schema(description = "댓글 내용", required = true)
    @field:NotBlank
    val commentText: String,

    @Schema(description = "대댓글인 경우 부모 댓글 ID", required = false)
    val parentCommentId: Long? = null
)
