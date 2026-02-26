package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface WeeklyContestFinalRepository: JpaRepository<WeeklyContestFinalEntity, Long> {

    @Query("""
        SELECT wcf
        FROM WeeklyContestFinalEntity wcf
        JOIN FETCH wcf.weeklyContestPost post
        JOIN FETCH post.member
        WHERE wcf.weeklyContest.id = :contestId
    """)
    fun findAllByWeeklyContestId(contestId: Long): List<WeeklyContestFinalEntity>

    @Query("""
        SELECT wcf
        FROM WeeklyContestFinalEntity wcf
        JOIN FETCH wcf.weeklyContestPost post
        JOIN FETCH post.member
        WHERE wcf.weeklyContest.id = :contestId
        ORDER BY wcf.ranking ASC
        LIMIT 1
    """)
    fun findFirstPrizePostByContestId(contestId: Long): WeeklyContestFinalEntity?

    fun existsByWeeklyContestPost(weeklyContestPostEntity: WeeklyContestPostEntity): Boolean
}
