package com.soongan.soonganbackend.soonganpersistence.storage.postLike

import com.soongan.soonganbackend.soonganpersistence.storage.comment.ContestTypeEnum
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
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

    @Transactional
    fun cancelLike(postId: Long, contestType: ContestTypeEnum, member: MemberEntity) {
        postLikeRepository.deleteByPostIdAndContestTypeAndMember(postId, contestType, member)
    }

    @Transactional(readOnly = true)
    fun getByPostIdAndContestTypeAndMember(postId: Long, contestType: ContestTypeEnum, member: MemberEntity): PostLikeEntity? {
        return postLikeRepository.findByPostIdAndContestTypeAndMember(postId, contestType, member)
    }
}
