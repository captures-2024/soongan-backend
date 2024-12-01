package com.soongan.soonganbackend.soonganapi.interfaces.postLike.dto.request

import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum


data class PostLikeRequestDto (
    val postId: Long,
    val contestType: ContestTypeEnum
)
