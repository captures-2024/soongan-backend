package com.soongan.soonganbackend.persistence.weeklyContest

import org.springframework.stereotype.Component

@Component
class WeeklyContestAdapter (
    private val weeklyContestRepository: WeeklyContestRepository
){

    fun getWeeklyContest(round: Int): WeeklyContestEntity? {
        return weeklyContestRepository.findByRound(round)
    }
}
