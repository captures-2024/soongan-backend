package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WeeklyContestPostAdapter(
    private val weeklyContestPostRepository: WeeklyContestPostRepository
) {

    @Transactional
    fun save(post: WeeklyContestPostEntity): WeeklyContestPostEntity {
        return weeklyContestPostRepository.save(post)
    }

    @Transactional(readOnly = true)
    fun getByIdOrNull(postId: Long): WeeklyContestPostEntity? {
        return weeklyContestPostRepository.findByIdOrNull(postId)
    }

    @Transactional(readOnly = true)
    fun getLatestPostWithSlicing(round: Int, page: Int, size: Int): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestRoundOrderByCreatedAtDesc(round, PageRequest.of(page, size))
    }

    @Transactional(readOnly = true)
    fun getOldestPostWithSlicing(round: Int, page: Int, size: Int): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestRoundOrderByCreatedAtAsc(round, PageRequest.of(page, size))
    }

    @Transactional(readOnly = true)
    fun getMostLikedPostWithSlicing(round: Int, page: Int, size: Int): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestRoundOrderByLikeCountDesc(round, PageRequest.of(page, size))
    }
}
