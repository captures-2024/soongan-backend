package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WeeklyContestFinalAdapter(
    private val weeklyContestFinRepository: WeeklyContestFinalRepository
) {
    @Transactional(readOnly = true)
    fun getFinalPostsByContestId(contestId: Long): List<WeeklyContestFinalEntity> {
        return weeklyContestFinRepository.findAllByWeeklyContestId(contestId)
    }

    @Transactional(readOnly = true)
    fun getFirstPrizePostByContestId(contestId: Long): WeeklyContestFinalEntity? {
        return weeklyContestFinRepository.findFirstPrizePostByContestId(contestId)
    }

    @Transactional(readOnly = true)
    fun isTop7Post(post: WeeklyContestPostEntity): Boolean {
        return weeklyContestFinRepository.existsByWeeklyContestPost(post)
    }

    @Transactional(readOnly = true)
    fun hasFinalData(contestId: Long): Boolean {
        return weeklyContestFinRepository.findAllByWeeklyContestId(contestId).isNotEmpty()
    }

    @Transactional
    fun saveAll(finals: List<WeeklyContestFinalEntity>): List<WeeklyContestFinalEntity> {
        return weeklyContestFinRepository.saveAll(finals)
    }
}