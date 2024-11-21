package com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentStatusEnum
import com.soongan.soonganbackend.soongansupport.util.dto.PageDto
import org.springframework.data.domain.Slice

data class GetCommentResponseDto(
    val postId: Long,
    val comments: List<CommentDto>,
    val pageInfo: PageDto
) {

    data class CommentDto(
        val commentId: Long,
        val memberId: Long,
        val memberNickname: String,
        val commentText: String,
        val parentCommentID: Long? = null,
        val commentStatus: CommentStatusEnum,
    )

    companion object {
        fun from(
            postId: Long,
            commentSlice: Slice<CommentEntity>
        ): GetCommentResponseDto {
            return GetCommentResponseDto(
                postId = postId,
                comments = commentSlice.content.map {
                    CommentDto(
                        commentId = it.id!!,
                        memberId = it.member.id!!,
                        memberNickname = it.member.nickname ?: "",
                        commentText = it.commentText,
                        parentCommentID = it.parentComment?.id,
                        commentStatus = it.commentStatus
                    )
                },
                pageInfo = PageDto(
                    page = commentSlice.number,
                    size = commentSlice.size,
                    hasNext = commentSlice.hasNext()
                )
            )
        }
    }
}
