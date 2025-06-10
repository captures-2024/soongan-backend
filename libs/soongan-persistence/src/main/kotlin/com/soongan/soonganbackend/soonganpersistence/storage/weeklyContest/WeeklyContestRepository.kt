package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface WeeklyContestRepository: JpaRepository<WeeklyContestEntity, Long> {
    fun findByRound(round: Int): WeeklyContestEntity?

    @Query("SELECT wc FROM WeeklyContestEntity wc WHERE wc.endAt <= :now")
    fun findEndedContests(now: LocalDateTime): List<WeeklyContestEntity>

    @Query("SELECT wc FROM WeeklyContestEntity wc WHERE wc.startAt <= :now AND wc.endAt > :now")
    fun findInProgressWeeklyContest(now: LocalDateTime): WeeklyContestEntity?

    fun findFirstByEndAtBeforeOrderByEndAtDesc(now: LocalDateTime): WeeklyContestEntity?
}
