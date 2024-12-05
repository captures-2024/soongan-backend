package com.soongan.soonganbackend.soonganapi.interfaces.commentLike.dto.request

import com.soongan.soonganbackend.soonganpersistence.storage.comment.ContestTypeEnum

data class CommentLikeRequestDto (
    val commentId: Long,
    val contestTypeEnum: ContestTypeEnum
)
