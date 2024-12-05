package com.soongan.soonganbackend.soonganapi.service.commentLike

import com.soongan.soonganbackend.soonganapi.helper.LikeInterface
import com.soongan.soonganbackend.soonganapi.interfaces.commentLike.dto.request.CommentLikeRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.commentLike.dto.response.CommentLikeResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.commentLike.CommentLikeAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service

@Service
class CommentLikeService(
    private val commentLikeAdapter: CommentLikeAdapter,
    private val commentAdapter: CommentAdapter
): LikeInterface<CommentLikeRequestDto, CommentLikeResponseDto> {

    @Override
    override fun addLike(loginMember: MemberEntity, request: CommentLikeRequestDto): CommentLikeResponseDto {
        // todo: DAILY Contest 추가되면 구조 리팩토링 필요할 듯 (중복 발생할 듯)
        if (request.contestTypeEnum == ContestTypeEnum.WEEKLY) {
            val updatedComment: CommentEntity = commentAdapter.getByIdOrNull(request.commentId)?.let { comment ->

                // 중복 좋아요 방지
               isDuplicateLike(comment.id!!, request.contestTypeEnum, loginMember)

                // 좋아요 추가
                commentLikeAdapter.addLike(
                    request.commentId,
                    request.contestTypeEnum,
                    member = loginMember
                )

                // 좋아요 개수 증가
                commentAdapter.save(
                    comment.copy(likeCount = comment.likeCount + 1)
                )
            } ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_COMMENT)

            return CommentLikeResponseDto(
                commentId = updatedComment.id!!,
                likeCount = updatedComment.likeCount
            )
        } else {
            throw SoonganException(StatusCode.SOONGAN_API_INVALID_CONTEST_TYPE)
        }
    }

    override fun cancelLike(loginMember: MemberEntity, request: CommentLikeRequestDto): CommentLikeResponseDto {
        if (request.contestTypeEnum == ContestTypeEnum.WEEKLY) {
            val updatedComment: CommentEntity = commentAdapter.getByIdOrNull(request.commentId)?.let { comment ->

                // 좋아요 취소
                commentLikeAdapter.cancelLike(comment.id!!, request.contestTypeEnum, loginMember)

                // 좋아요 개수 감소
                commentAdapter.save(
                    comment.copy(likeCount = comment.likeCount - 1)
                )
            } ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_COMMENT)

            return CommentLikeResponseDto(
                commentId = updatedComment.id!!,
                likeCount = updatedComment.likeCount
            )
        } else {
            throw SoonganException(StatusCode.SOONGAN_API_INVALID_CONTEST_TYPE)
        }
    }

    override fun isDuplicateLike(id: Long, contestType: ContestTypeEnum, loginMember: MemberEntity) {
        if (commentLikeAdapter.existsByCommentIdAndContestTypeAndMember(id, contestType, loginMember)) {
            throw SoonganException(StatusCode.SOONGAN_API_DUPLICATED_LIKE)
        }
    }
}
