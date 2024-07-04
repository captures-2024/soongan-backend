package com.soongan.soonganbackend.persistence.postLike

import com.soongan.soonganbackend.persistence.member.MemberEntity
import com.soongan.soonganbackend.util.domain.ContestTypeEnum
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

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

    @Transactional(readOnly = true)
    fun getByPostIdAndContestTypeAndMember(postId: Long, contestType: ContestTypeEnum, member: MemberEntity): PostLikeEntity? {
        return postLikeRepository.findByPostIdAndContestTypeAndMember(postId, contestType, member)
    }
}
