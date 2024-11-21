package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import org.springframework.data.domain.Page
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
    fun getLatestPostWithSlicing(
        weeklyContest: WeeklyContestEntity,
        page: Int,
        size: Int
    ): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestOrderByCreatedAtDesc(
            weeklyContest,
            PageRequest.of(page, size)
        )
    }

    @Transactional(readOnly = true)
    fun getOldestPostWithSlicing(
        weeklyContest: WeeklyContestEntity,
        page: Int,
        size: Int
    ): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestOrderByCreatedAtAsc(
            weeklyContest,
            PageRequest.of(page, size)
        )
    }

    @Transactional(readOnly = true)
    fun getMostLikedPostWithSlicing(
        weeklyContest: WeeklyContestEntity,
        page: Int,
        size: Int
    ): Slice<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByWeeklyContestOrderByLikeCountDesc(
            weeklyContest,
            PageRequest.of(page, size)
        )
    }

    @Transactional(readOnly = true)
    fun countRegisteredPostByMember(
        weeklyContest: WeeklyContestEntity,
        member: MemberEntity,
    ): Int {
        return weeklyContestPostRepository.countByWeeklyContestAndMember(weeklyContest, member)
    }

    @Transactional(readOnly = true)
    fun getAllWeeklyContestPostByMember(
        member: MemberEntity,
        page: Int,
        size: Int
    ): Page<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByMember(
            member,
            PageRequest.of(page, size)
        )
    }

    @Transactional(readOnly = true)
    fun getAllWeeklyContestPostByMemberAndWeeklyContest(
        member: MemberEntity,
        weeklyContest: WeeklyContestEntity
    ): List<WeeklyContestPostEntity> {
        return weeklyContestPostRepository.findAllByMemberAndWeeklyContestOrderByCreatedAtDesc(
            member,
            weeklyContest
        )
    }

    @Transactional
    fun deleteWeeklyContestPost(postId: Long) {
        weeklyContestPostRepository.deleteById(postId)
    }
}
