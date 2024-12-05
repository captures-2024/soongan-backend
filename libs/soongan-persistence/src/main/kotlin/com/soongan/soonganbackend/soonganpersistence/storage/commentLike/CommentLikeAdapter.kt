package com.soongan.soonganbackend.soonganpersistence.storage.commentLike

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CommentLikeAdapter(
    private val commentLikeRepository: CommentLikeRepository
) {

    @Transactional
    fun addLike(commentId: Long, contestType: ContestTypeEnum, member: MemberEntity): CommentLikeEntity {
        return commentLikeRepository.save(
            CommentLikeEntity(
                commentId = commentId,
                contestType = contestType,
                member = member
            )
        )
    }

    @Transactional
    fun cancelLike(commentId: Long, contestType: ContestTypeEnum, member: MemberEntity) {
        commentLikeRepository.deleteByCommentIdAndContestTypeAndMember(commentId, contestType, member)
    }

    fun existsByCommentIdAndContestTypeAndMember(commentId: Long, contestType: ContestTypeEnum, member: MemberEntity): Boolean {
        return commentLikeRepository.existsByCommentIdAndContestTypeAndMember(commentId, contestType, member)
    }

}
