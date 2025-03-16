package com.soongan.soonganbackend.soonganapi.interfaces.postLike.dto.request

import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(description = "게시글 좋아요 요청 DTO")
data class PostLikeRequestDto (
    @Schema(description = "게시글 ID", required = true)
    @field:NotNull
    val postId: Long,

    @Schema(description = "대회 타입 (WEEKLY/DAILY)", required = true)
    @field:NotNull
    val contestType: ContestTypeEnum
)
