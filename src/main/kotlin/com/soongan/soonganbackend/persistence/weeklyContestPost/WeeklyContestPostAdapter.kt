package com.soongan.soonganbackend.persistence.weeklyContestPost

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component

@Component
class WeeklyContestPostAdapter(
    private val weeklyContestPostRepository: WeeklyContestPostRepository
) {

    fun getLatestPostWithSlicing(round: Int, page: Int, size: Int): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestRoundOrderByCreatedAtDesc(round, PageRequest.of(page, size))
    }

    fun getOldestPostWithSlicing(round: Int, page: Int, size: Int): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestRoundOrderByCreatedAtAsc(round, PageRequest.of(page, size))
    }

    fun getMostLikedPostWithSlicing(round: Int, page: Int, size: Int): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestRoundOrderByLikeCountDesc(round, PageRequest.of(page, size))
    }
}
