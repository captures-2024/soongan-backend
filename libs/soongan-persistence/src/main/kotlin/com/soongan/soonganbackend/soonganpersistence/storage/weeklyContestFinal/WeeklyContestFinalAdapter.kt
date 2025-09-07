package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import org.springframework.stereotype.Component

@Component
class WeeklyContestFinalAdapter(
    private val weeklyContestFinRepository: WeeklyContestFinalRepository
) {
    fun getFinalPostsByContestId(contestId: Long): List<WeeklyContestFinalEntity> {
        return weeklyContestFinRepository.findAllByWeeklyContestId(contestId)
    }

    fun getFirstPrizePostByContestId(contestId: Long): WeeklyContestFinalEntity? {
        return weeklyContestFinRepository.findFirstPrizePostByContestId(contestId)
    }

    fun isTop7Post(post: WeeklyContestPostEntity): Boolean {
        return weeklyContestFinRepository.existsByWeeklyContestPost(post)
    }
}