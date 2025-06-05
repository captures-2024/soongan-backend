package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal

import org.springframework.data.jpa.repository.JpaRepository

interface WeeklyContestFinalRepository: JpaRepository<WeeklyContestFinalEntity, Long> {

    fun findAllByWeeklyContestId(contestId: Long): List<WeeklyContestFinalEntity>
}
