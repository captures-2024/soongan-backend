package com.soongan.soonganbackend.soonganapi.interfaces.postLike.dto.request

import com.soongan.soonganbackend.soonganpersistence.storage.comment.ContestTypeEnum


data class PostLikeRequestDto (
    val postId: Long,
    val contestType: ContestTypeEnum
)
