package com.soongan.soonganbackend.soonganpersistence.storage.persistence.weeklyContest

import org.springframework.data.jpa.repository.JpaRepository

interface WeeklyContestRepository: JpaRepository<WeeklyContestEntity, Long> {
    fun findByRound(round: Int): WeeklyContestEntity?
}
