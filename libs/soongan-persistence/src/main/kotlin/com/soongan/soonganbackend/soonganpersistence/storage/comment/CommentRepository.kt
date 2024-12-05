package com.soongan.soonganbackend.soonganpersistence.storage.comment

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<CommentEntity, Long> {

    fun findAllByPostIdAndContestTypeOrderByCreatedAt(postId: Long, contestType: ContestTypeEnum, pageable: Pageable): Slice<CommentEntity>

    fun findAllByMemberAndContestTypeOrderByCreatedAtDesc(member: MemberEntity, contestType: ContestTypeEnum, pageable: Pageable): Slice<CommentEntity>

    fun findAllByParentCommentIdAndContestTypeOrderByCreatedAt(parentCommentId: Long, contestType: ContestTypeEnum, pageable: Pageable): Slice<CommentEntity>
}
