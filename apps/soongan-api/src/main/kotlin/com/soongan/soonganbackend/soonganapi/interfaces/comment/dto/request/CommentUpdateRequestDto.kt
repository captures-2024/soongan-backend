package com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request

import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import jakarta.validation.constraints.NotEmpty

data class CommentUpdateRequestDto(
    val contestType: ContestTypeEnum,
    val postId: Long,
    val commentId: Long,
    @field:NotEmpty
    val commentText: String,
)
