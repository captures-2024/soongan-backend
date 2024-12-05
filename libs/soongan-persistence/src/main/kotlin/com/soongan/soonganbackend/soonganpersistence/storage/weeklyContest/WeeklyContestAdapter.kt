package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class WeeklyContestAdapter (
    private val weeklyContestRepository: WeeklyContestRepository
){

    fun getWeeklyContest(round: Int): WeeklyContestEntity? {
        return weeklyContestRepository.findByRound(round)
    }

    fun getInProgressWeeklyContest(now: LocalDateTime = LocalDateTime.now()): WeeklyContestEntity? {
        return weeklyContestRepository.findInProgressWeeklyContest(now)
    }

    fun getLatestEndedWeeklyContest(now: LocalDateTime = LocalDateTime.now()): WeeklyContestEntity? {
        return weeklyContestRepository.findLatestEndedWeeklyContest(now)
    }
}
