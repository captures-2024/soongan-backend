package com.soongan.soonganbackend.soonganapi.service.comment.validator

import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Component

@Component
class CommentValidator(
    private val commentAdapter: CommentAdapter
) {

    // 자기가 작성한 댓글인지 확인
    fun checkMyComment(loginMember: MemberEntity, commentId: Long): CommentEntity {
        val comment = commentAdapter.getByIdOrNull(commentId) ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_COMMENT)
        if (comment.member != loginMember) {
            throw SoonganException(StatusCode.SOONGAN_API_NOT_OWNER_COMMENT)
        }
        return comment
    }
}
