package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal

import org.springframework.stereotype.Component

@Component
class WeeklyContestFinalAdapter(
    private val weeklyContestFinRepository: WeeklyContestFinalRepository
) {
    fun getFinalPostsByContestId(contestId: Long): List<WeeklyContestFinalEntity> {
        return weeklyContestFinRepository.findAllByWeeklyContestId(contestId)
    }
}