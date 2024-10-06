package com.soongan.soonganbackend.soonganpersistence.storage.persistence.weeklyContestPost

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface WeeklyContestPostRepository: JpaRepository<WeeklyContestPostEntity, Long> {

    // 현재 회차 최신순
    fun findAllByWeeklyContestRoundOrderByCreatedAtDesc(round: Int, pageable: Pageable): Slice<WeeklyContestPostEntity>

    // 현재 회차 오래된 순
    fun findAllByWeeklyContestRoundOrderByCreatedAtAsc(round: Int, pageable: Pageable): Slice<WeeklyContestPostEntity>

    // 현재 회차 좋아요 많은 순
    fun findAllByWeeklyContestRoundOrderByLikeCountDesc(round: Int, pageable: Pageable): Slice<WeeklyContestPostEntity>
}
