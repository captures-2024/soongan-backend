package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface WeeklyContestRepository: JpaRepository<WeeklyContestEntity, Long> {
    // 라운드 오름차순으로 정렬된 모든 주간 콘테스트를 조회
    fun findAllByOrderByRoundAsc(): List<WeeklyContestEntity>

    fun findByRound(round: Int): WeeklyContestEntity?

    @Query("SELECT wc FROM WeeklyContestEntity wc WHERE wc.endAt <= :now")
    fun findEndedContests(now: LocalDateTime): List<WeeklyContestEntity>

    @Query("SELECT wc FROM WeeklyContestEntity wc WHERE wc.startAt <= :now AND wc.endAt > :now")
    fun findInProgressWeeklyContest(now: LocalDateTime): WeeklyContestEntity?

    @Query("SELECT wc FROM WeeklyContestEntity wc WHERE wc.endAt < :now ORDER BY wc.endAt DESC LIMIT 1")
    fun findLatestEndedWeeklyContest(now: LocalDateTime): WeeklyContestEntity?
}
