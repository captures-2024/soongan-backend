package com.soongan.soonganbackend.soonganapi.service.home

import com.soongan.soonganbackend.soonganapi.interfaces.home.dto.response.HomeResponseDto
import com.soongan.soonganbackend.soonganapi.service.weeklyContest.validator.WeeklyContestValidator
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.type.WeeklyContestPostAndIsLiked
import org.springframework.stereotype.Service

@Service
class HomeService(
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val weeklyContestValidator: WeeklyContestValidator,
) {

    fun getHome(loginMember: MemberEntity?): HomeResponseDto {
        val weeklyContest: WeeklyContestEntity = weeklyContestValidator.getWeeklyContestIfValidRound()
        val contestStatus = weeklyContestValidator.determineContestStatus(weeklyContest)

        if (loginMember == null) {
            return HomeResponseDto.fromWeeklyContest(weeklyContest, contestStatus, emptyList())
        }

        val homeWeeklyContestPostList: List<WeeklyContestPostAndIsLiked> =
            weeklyContestPostAdapter.getHomePostsWithIsLiked(
                loginMember,
                weeklyContest
            )

        return HomeResponseDto.fromWeeklyContest(
            weeklyContest = weeklyContest,
            status = contestStatus,
            postInfo = homeWeeklyContestPostList
        )
    }
}
