package com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentStatusEnum
import com.soongan.soonganbackend.soongansupport.util.dto.PageDto
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Slice

@Schema(description = "댓글 조회 응답 DTO")
data class GetCommentResponseDto(
    @Schema(description = "게시글 ID", required = true)
    val postId: Long,

    @Schema(description = "게시글 댓글 목록", required = true)
    val comments: List<CommentDto>,

    @Schema(description = "페이지네이션 정보", required = true)
    val pageInfo: PageDto
) {
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
                        memberId = it.member.id,
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

data class GetCommentReplyResponseDto(
    val comments: List<CommentDto>,
    val pageInfo: PageDto
) {
    companion object {
        fun from(
            commentSlice: Slice<CommentEntity>
        ): GetCommentReplyResponseDto {
            return GetCommentReplyResponseDto(
                comments = commentSlice.content.map {
                    CommentDto(
                        commentId = it.id!!,
                        memberId = it.member.id,
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

data class CommentDto(
    val commentId: Long,
    val memberId: Long,
    val memberNickname: String,
    val commentText: String,
    val parentCommentID: Long? = null,
    val commentStatus: CommentStatusEnum,
)
