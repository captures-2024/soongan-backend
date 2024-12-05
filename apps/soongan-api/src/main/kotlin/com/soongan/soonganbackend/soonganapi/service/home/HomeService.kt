package com.soongan.soonganbackend.soonganapi.service.home

import com.soongan.soonganbackend.soonganapi.interfaces.home.dto.response.HomeResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class HomeService(
    private val weeklyContestAdapter: WeeklyContestAdapter,
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter
) {

    fun getHome(loginMember: MemberEntity): HomeResponseDto {
        val now = LocalDateTime.now()

        // 진행 중인 Contest 없으면, 가장 최신 종료된 Contest 조회
        val weeklyContest: WeeklyContestEntity = weeklyContestAdapter.getInProgressWeeklyContest(now)
            ?: weeklyContestAdapter.getLatestEndedWeeklyContest(now)
            ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST)


        val homeWeeklyContestPostList: List<WeeklyContestPostEntity> =
            weeklyContestPostAdapter.getAllWeeklyContestPostByMemberAndWeeklyContest(
                loginMember,
                weeklyContest
            )

        return HomeResponseDto.fromWeeklyContest(weeklyContest, homeWeeklyContestPostList)
    }
}
