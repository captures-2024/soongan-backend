package com.soongan.soonganbackend.service.weeklyContest

import com.soongan.soonganbackend.`interface`.weeklyContest.dto.WeeklyContestPostResponseDto
import com.soongan.soonganbackend.persistence.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.persistence.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.persistence.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.persistence.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.service.weeklyContest.WeeklyContestPostOrderCriteriaEnum.*
import com.soongan.soonganbackend.util.common.dto.MemberInfoDto
import com.soongan.soonganbackend.util.common.dto.PageDto
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

@Service
class WeeklyContestService (
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val weeklyContestAdapter: WeeklyContestAdapter
){

    fun getWeeklyContestPost(
        round: Int,
        orderCriteria: WeeklyContestPostOrderCriteriaEnum,
        page: Int,
        pageSize: Int
    ): WeeklyContestPostResponseDto {
        val weeklyContest: WeeklyContestEntity = weeklyContestAdapter.getWeeklyContest(round) ?:
            throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST)

        val weeklyContestPostSlice: Slice<WeeklyContestPostEntity> = when (orderCriteria) {
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
            round = round,
            subject = weeklyContest.subject,
            posts = weeklyContestPostSlice.content.map {
                WeeklyContestPostResponseDto.WeeklyContestPostDto(
                    memberInfo = MemberInfoDto.from(it.member),
                    postId = it.id!!,
                    imageUrl = it.imageUrl,
                )
            },
            pageInfo = PageDto(
                page = weeklyContestPostSlice.number,
                size = weeklyContestPostSlice.size,
                hasNext = weeklyContestPostSlice.hasNext()
            )
        )
    }
}
