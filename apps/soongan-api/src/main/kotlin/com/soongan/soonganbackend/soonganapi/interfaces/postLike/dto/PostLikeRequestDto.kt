package com.soongan.soonganbackend.soonganapi.interfaces.postLike.dto

import com.soongan.soonganbackend.soonganpersistence.storage.persistence.comment.ContestTypeEnum


data class PostLikeRequestDto (
    val postId: Long,
    val contestType: ContestTypeEnum
)
