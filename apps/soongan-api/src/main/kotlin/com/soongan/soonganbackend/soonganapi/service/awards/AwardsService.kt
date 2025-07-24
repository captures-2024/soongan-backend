package com.soongan.soonganbackend.soonganapi.service.awards

import com.soongan.soonganbackend.soonganapi.interfaces.awards.dto.response.AwardsDetailResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.awards.dto.response.WeeklyContestAwardsResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal.WeeklyContestFinalAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AwardsService(
    private val weeklyContestAdapter: WeeklyContestAdapter,
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val weeklyContestFinalAdapter: WeeklyContestFinalAdapter
){

    @Transactional(readOnly = true)
    fun getWeeklyContestAwards(): WeeklyContestAwardsResponseDto {
        // 1차 투표가 끝난 주간 콘테스트들만 조회
        val weeklyContestList: List<WeeklyContestEntity> = weeklyContestAdapter.getEndedWeeklyContests()
        return weeklyContestList.map { contest ->
            val firstPrizePost = weeklyContestFinalAdapter.getFirstPrizePostByContestId(contest.id)
                ?: throw SoonganException(
                    StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST,
                    "해당 콘테스트의 1등 게시글이 존재하지 않습니다. contestId: ${contest.id}"
                )

            WeeklyContestAwardsResponseDto.WeeklyContestDto.from(
                entity = contest,
                thumbnailImageUrl = firstPrizePost.weeklyContestPost.imageUrl
            )
        }.let { WeeklyContestAwardsResponseDto(it) }
    }

    @Transactional(readOnly = true)
    fun getAwardsDetail(contestId: Long): AwardsDetailResponseDto {
        val contest = weeklyContestAdapter.getWeeklyContestById(contestId)
            ?: throw SoonganException(
                StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST,
                "해당 주간 콘테스트가 존재하지 않습니다. contestId: $contestId"
            )
        val postsCount = weeklyContestPostAdapter.countByWeeklyContestId(contestId)
        val top7Posts = weeklyContestFinalAdapter.getFinalPostsByContestId(contestId)
        return AwardsDetailResponseDto.from(contest, postsCount, top7Posts)
    }
}