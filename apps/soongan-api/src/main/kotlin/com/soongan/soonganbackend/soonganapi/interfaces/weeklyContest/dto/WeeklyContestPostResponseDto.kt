package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto

import com.soongan.soonganbackend.soongansupport.util.dto.PageDto


data class WeeklyContestPostResponseDto (
    val round: Int,
    val subject: String,
    val posts: List<WeeklyContestPostDto>,
    val pageInfo: PageDto
) {

    data class WeeklyContestPostDto (
        val nickname: String,
        val profileImageUrl: String,
        val postId: Long,
        val imageUrl: String,
    )

}
