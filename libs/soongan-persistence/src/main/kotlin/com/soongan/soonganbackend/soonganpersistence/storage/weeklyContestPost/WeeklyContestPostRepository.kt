package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface WeeklyContestPostRepository : JpaRepository<WeeklyContestPostEntity, Long> {

    // 현재 회차 최신순
    fun findAllByWeeklyContestOrderByCreatedAtDesc(
        weeklyContestEntity: WeeklyContestEntity,
        pageable: Pageable
    ): Slice<WeeklyContestPostEntity>

    // 현재 회차 오래된 순
    fun findAllByWeeklyContestOrderByCreatedAtAsc(
        weeklyContestEntity: WeeklyContestEntity,
        pageable: Pageable
    ): Slice<WeeklyContestPostEntity>

    // 현재 회차 좋아요 많은 순
    fun findAllByWeeklyContestOrderByLikeCountDesc(
        weeklyContestEntity: WeeklyContestEntity,
        pageable: Pageable
    ): Slice<WeeklyContestPostEntity>

    fun countByWeeklyContestAndMember(
        weeklyContestEntity: WeeklyContestEntity,
        member: MemberEntity
    ): Int
}
