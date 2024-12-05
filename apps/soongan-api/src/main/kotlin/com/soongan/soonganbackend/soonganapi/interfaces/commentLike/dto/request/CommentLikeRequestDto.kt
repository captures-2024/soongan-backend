package com.soongan.soonganbackend.soonganapi.interfaces.commentLike.dto.request

import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum

data class CommentLikeRequestDto (
    val commentId: Long,
    val contestTypeEnum: ContestTypeEnum
)
