package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity

data class WeeklyContestPostResponseDto(
    val memberId: Long,
    val postId: Long,
    val title: String,
    val imageUrl: String,
    val nickname: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val commentCount: Int,
) {

    companion object {
        fun from(memberId: Long, weeklyContestPost: WeeklyContestPostEntity, isLiked: Boolean): WeeklyContestPostResponseDto {
            return WeeklyContestPostResponseDto(
                memberId = memberId,
                postId = weeklyContestPost.id!!,
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
