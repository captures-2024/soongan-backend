package com.soongan.soonganbackend.service.weeklyContest

import com.soongan.soonganbackend.`interface`.weeklyContest.dto.WeeklyContestPostResponseDto
import com.soongan.soonganbackend.persistence.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.persistence.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.service.weeklyContest.WeeklyContestPostOrderCriteriaEnum.*
import com.soongan.soonganbackend.util.common.dto.MemberInfoDto
import com.soongan.soonganbackend.util.common.dto.PageDto
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class WeeklyContestService (
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter
){

    fun getWeeklyContestPost(
        round: Int,
        orderCriteria: WeeklyContestPostOrderCriteriaEnum,
        page: Int,
        pageSize: Int
    ): WeeklyContestPostResponseDto {
        val weeklyContestPost: Slice<WeeklyContestPostEntity> = when (orderCriteria) {
            LATEST -> {
                weeklyContestPostAdapter.getLatestPostWithSlicing(round, page, pageSize)
            }
            MOST_LIKED -> {
                weeklyContestPostAdapter.getMostLikedPostWithSlicing(round, page, pageSize)
            }
            OLDEST -> {
                weeklyContestPostAdapter.getOldestPostWithSlicing(round, page, pageSize)
            }
        }

        return WeeklyContestPostResponseDto(
            posts = weeklyContestPost.content.map {
                WeeklyContestPostResponseDto.WeeklyContestPostDto(
                    memberInfo = MemberInfoDto.from(it.member),
                    postId = it.id!!,
                    imageUrl = it.imageUrl,
                )
            },
            pageInfo = PageDto(
                page = weeklyContestPost.number,
                size = weeklyContestPost.size,
                hasNext = weeklyContestPost.hasNext()
            )
        )
    }
}
