package com.soongan.soonganbackend.soonganpersistence.storage.postLike

import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class PostLikeAdapter(
    private val postLikeRepository: PostLikeRepository
) {

    @Transactional
    fun addLike(postId: Long, contestType: ContestTypeEnum, member: MemberEntity): PostLikeEntity {
        return postLikeRepository.save(
            PostLikeEntity(
                postId = postId,
                contestType = contestType,
                member = member
            )
        )
    }

    @Transactional
    fun cancelLike(postId: Long, contestType: ContestTypeEnum, member: MemberEntity) {
        postLikeRepository.deleteByPostIdAndContestTypeAndMember(postId, contestType, member)
    }

    @Transactional(readOnly = true)
    fun existsByPostIdAndContestTypeAndMember(postId: Long, contestType: ContestTypeEnum, member: MemberEntity): Boolean {
        return postLikeRepository.existsByPostIdAndContestTypeAndMember(postId, contestType, member)
    }

    @Transactional(readOnly = true)
    fun countLikesByPostIdAndPeriod(
        postId: Long,
        contestType: ContestTypeEnum,
        startAt: LocalDateTime,
        endAt: LocalDateTime
    ): Long {
        return postLikeRepository.countLikesByPostIdAndPeriod(postId, contestType, startAt, endAt)
    }
}
