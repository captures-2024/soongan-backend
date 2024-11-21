package com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentStatusEnum
import com.soongan.soonganbackend.soongansupport.util.dto.PageDto
import org.springframework.data.domain.Slice

data class GetMyCommentResponseDto(
    val comments: List<MyCommentDto>,
    val pageInfo: PageDto
) {
    data class MyCommentDto(
        val postId: Long,
        val commentId: Long,
        val contestType: String,
        val commentText: String,
        val commentStatus: CommentStatusEnum
    )

    companion object {
        fun from(
           commentSlice: Slice<CommentEntity>
        ): GetMyCommentResponseDto {
            return GetMyCommentResponseDto(
                comments = commentSlice.content.map {
                    MyCommentDto(
                        postId = it.postId,
                        commentId = it.id!!,
                        contestType = it.contestType.name,
                        commentText = it.commentText,
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
