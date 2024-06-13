package com.soongan.soonganbackend.`interface`.weeklyContest.dto

import com.soongan.soonganbackend.util.common.dto.MemberInfoDto
import com.soongan.soonganbackend.util.common.dto.PageDto

data class WeeklyContestPostResponseDto (
    val posts: List<WeeklyContestPostDto>,
    val pageInfo: PageDto
) {

    data class WeeklyContestPostDto (
        val memberInfo: MemberInfoDto,
        val postId: Long,
        val imageUrl: String,
    )
}
