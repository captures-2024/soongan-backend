package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "주간 콘테스트 게시글 조회 응답 DTO")
data class WeeklyContestPostResponseDto(
    @Schema(description = "조회 요청한 회원의 ID", type = "Long", nullable = true)
    val memberId: Long?,

    @Schema(description = "게시글 ID", type = "Long")
    val postId: Long,

    @Schema(description = "게시글 제목", type = "String")
    val title: String,

    @Schema(description = "게시글 이미지 URL", type = "String")
    val imageUrl: String,

    @Schema(description = "게시글 작성자 닉네임", type = "String")
    val nickname: String,

    @Schema(description = "게시글 좋아요 수", type = "Int")
    val likeCount: Int,

    @Schema(description = "조회 요청한 회원이 해당 게시글을 좋아요 했는지 여부", type = "Boolean")
    val isLiked: Boolean,

    @Schema(description = "게시글 댓글 수", type = "Int")
    val commentCount: Int,
) {

    companion object {
        fun from(memberId: Long? = null, weeklyContestPost: WeeklyContestPostEntity, isLiked: Boolean = false): WeeklyContestPostResponseDto {
            return WeeklyContestPostResponseDto(
                memberId = memberId,
                postId = weeklyContestPost.id,
                title = weeklyContestPost.title,
                imageUrl = weeklyContestPost.imageUrl,
                nickname = weeklyContestPost.member.nickname!!,
                likeCount = weeklyContestPost.likeCount,
                isLiked = isLiked,
                commentCount = weeklyContestPost.commentCount
            )
        }
    }
}
