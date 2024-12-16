package com.soongan.soonganbackend.soonganapi.service.weeklyContest

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class WeeklyContestValidator(
    private val weeklyContestAdapter: WeeklyContestAdapter
) {

    /**
     * 주어진 라운드에 해당하는 주간 콘테스트를 반환한다.
     * 라운드가 주어지지 않으면, 현재 진행 중인 주간 콘테스트를 반환한다.
     * 현재 진행 중인 주간 콘테스트가 없으면, 가장 최신 종료된 주간 콘테스트를 반환한다.
     */
    fun getWeeklyContestIfValidRound(round: Int? = null): WeeklyContestEntity {
        val now = LocalDateTime.now()

        return round?.let {
            weeklyContestAdapter.getWeeklyContest(round)
                ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST)
        }
            ?: weeklyContestAdapter.getInProgressWeeklyContest(now)
        ?: weeklyContestAdapter.getLatestEndedWeeklyContest(now)
        ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST)
    }
}
