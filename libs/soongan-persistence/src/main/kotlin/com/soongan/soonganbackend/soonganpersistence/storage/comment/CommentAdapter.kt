package com.soongan.soonganbackend.soonganpersistence.storage.comment

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CommentAdapter(
    private val commentRepository: CommentRepository
) {

    @Transactional
    fun save(comment: CommentEntity): CommentEntity {
        return commentRepository.save(comment)
    }

    @Transactional(readOnly = true)
    fun getByIdOrNull(commentId: Long): CommentEntity? {
        return commentRepository.findByIdOrNull(commentId)
    }

    @Transactional(readOnly = true)
    fun getPostComments(postId: Long, contestType: ContestTypeEnum, page: Int, size: Int): Slice<CommentEntity> {
        return commentRepository.findAllByPostIdAndContestTypeOrderByCreatedAt(postId, contestType, PageRequest.of(page, size))
    }

    @Transactional(readOnly = true)
    fun getPostCommentsReplies(parentCommentId: Long, contestType: ContestTypeEnum, page: Int, size: Int): Slice<CommentEntity> {
        return commentRepository.findAllByParentCommentIdAndContestTypeOrderByCreatedAt(parentCommentId, contestType, PageRequest.of(page, size))
    }

    @Transactional(readOnly = true)
    fun getMyComments(member: MemberEntity, contestType: ContestTypeEnum, page: Int, size: Int): Slice<CommentEntity> {
        return commentRepository.findAllByMemberAndContestTypeOrderByCreatedAtDesc(member, contestType, PageRequest.of(page, size))
    }
}
