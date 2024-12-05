package com.soongan.soonganbackend.soonganpersistence.storage.commentLike

import com.soongan.soonganbackend.soonganpersistence.storage.comment.ContestTypeEnum
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikeRepository: JpaRepository<CommentLikeEntity, Long> {

    fun existsByCommentIdAndContestTypeAndMember(commentId: Long, contestType: ContestTypeEnum, member: MemberEntity): Boolean

    fun deleteByCommentIdAndContestTypeAndMember(commentId: Long, contestType: ContestTypeEnum, member: MemberEntity)
}
