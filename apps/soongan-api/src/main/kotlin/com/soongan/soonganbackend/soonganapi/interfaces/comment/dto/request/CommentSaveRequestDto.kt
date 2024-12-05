package com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request

import com.soongan.soonganbackend.soonganpersistence.storage.comment.ContestTypeEnum
import jakarta.validation.constraints.NotEmpty

data class CommentSaveRequestDto (
    val contestType: ContestTypeEnum,
    val postId: Long,
    @field:NotEmpty
    val commentText: String,
    val parentCommentId: Long? = null
)
