package com.soongan.soonganbackend.soonganapi.service.weeklyContest

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Component

@Component
class WeeklyContestValidator(
    private val weeklyContestAdapter: WeeklyContestAdapter
) {

    fun getWeeklyContestIfValidRound(round: Int): WeeklyContestEntity {
        return weeklyContestAdapter.getWeeklyContest(round)
            ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST)
    }
}
