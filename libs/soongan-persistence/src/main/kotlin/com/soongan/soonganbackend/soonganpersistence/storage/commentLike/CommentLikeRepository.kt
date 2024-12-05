package com.soongan.soonganbackend.soonganpersistence.storage.commentLike

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikeRepository: JpaRepository<CommentLikeEntity, Long> {

    fun existsByCommentIdAndContestTypeAndMember(commentId: Long, contestType: ContestTypeEnum, member: MemberEntity): Boolean

    fun deleteByCommentIdAndContestTypeAndMember(commentId: Long, contestType: ContestTypeEnum, member: MemberEntity)
}
