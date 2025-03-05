package com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request

import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "댓글 수정 요청 DTO")
data class CommentUpdateRequestDto(
    @Schema(description = "대회 타입 (weekly/daily)", required = true)
    @field:NotBlank
    val contestType: ContestTypeEnum,

    @Schema(description = "게시글 ID", required = true)
    @field:NotBlank
    val postId: Long,

    @Schema(description = "수정할 댓글 ID", required = true)
    @field:NotBlank
    val commentId: Long,

    @Schema(description = "수정 내용", required = true)
    @field:NotBlank
    val commentText: String,
)
