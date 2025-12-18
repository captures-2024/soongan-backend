package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class WeeklyContestAdapter (
    private val weeklyContestRepository: WeeklyContestRepository
){

    fun getAllWeeklyContests(): List<WeeklyContestEntity> {
        return weeklyContestRepository.findAllByOrderByRoundDesc()
    }

    fun getEndedWeeklyContests(now: LocalDateTime = LocalDateTime.now()): List<WeeklyContestEntity> {
        return weeklyContestRepository.findEndedContests(now)
    }

    fun getWeeklyContestById(id: Long): WeeklyContestEntity? {
        return weeklyContestRepository.findById(id).orElse(null)
    }

    fun getWeeklyContestByRound(round: Int): WeeklyContestEntity? {
        return weeklyContestRepository.findByRound(round)
    }

    fun getInProgressWeeklyContest(now: LocalDateTime = LocalDateTime.now()): WeeklyContestEntity? {
        return weeklyContestRepository.findInProgressWeeklyContest(now)
    }

    fun getLatestEndedWeeklyContest(now: LocalDateTime = LocalDateTime.now()): WeeklyContestEntity? {
        return weeklyContestRepository.findFirstByEndAtBeforeOrderByEndAtDesc(now)
    }

    fun getLatestRound(): Int {
        return weeklyContestRepository.findFirstByOrderByRoundDesc()?.round ?: 0
    }

    // admin용
    fun save(weeklyContestEntity: WeeklyContestEntity): WeeklyContestEntity {
        return weeklyContestRepository.save(weeklyContestEntity)
    }

    // admin용
    fun deleteWeeklyContestById(contestId: Long) {
        weeklyContestRepository.deleteById(contestId)
    }
}
