package com.soongan.soonganbackend.interfaces.postLike.dto

import com.soongan.soonganbackend.util.domain.ContestTypeEnum

data class PostLikeRequestDto (
    val postId: Long,
    val contestType: ContestTypeEnum
)
