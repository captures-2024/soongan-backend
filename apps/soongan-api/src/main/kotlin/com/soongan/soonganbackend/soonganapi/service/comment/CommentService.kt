package com.soongan.soonganbackend.soonganapi.service.comment

import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request.CommentSaveRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request.CommentUpdateRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.response.GetCommentResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.response.GetMyCommentResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.response.GetCommentReplyResponseDto
import com.soongan.soonganbackend.soonganapi.service.comment.validator.CommentValidator
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentStatusEnum
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganredis.constant.RedisStreamKey
import com.soongan.soonganbackend.soonganredis.producer.RedisMessageProducer
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soongansupport.util.dto.Message
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentAdapter: CommentAdapter,
    private val fcmTokenAdapter: FcmTokenAdapter,
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val commentValidator: CommentValidator,
    private val redisMessageProducer: RedisMessageProducer
) {

    @Transactional
    fun saveComment(loginMember: MemberEntity, request: CommentSaveRequestDto) {
        val weeklyContestPost = weeklyContestPostAdapter.getByIdOrNull(request.postId)
            ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)

        val parentComment: CommentEntity? = request.parentCommentId?.let {
            commentAdapter.getByIdOrNull(it) ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_PARENT_COMMENT)
        }

        val comment = CommentEntity(
            postId = request.postId,
            contestType = request.contestType,
            commentText = request.commentText,
            parentComment = parentComment,
            member = loginMember
        )

        commentAdapter.save(comment)

        // 댓글 수 증가
        weeklyContestPostAdapter.save(weeklyContestPost.copy(
            commentCount = weeklyContestPost.commentCount + 1
            )
        )

        val fcmTokens = fcmTokenAdapter.findByMemberId(weeklyContestPost.member.id!!)
        fcmTokens.forEach { fcmToken ->
            val message = Message.createCommentMessage(
                token = fcmToken.token,
                postId = request.postId
            )
            redisMessageProducer.sendMessage(RedisStreamKey.SOONGAN_NOTI, message)
        }
    }

    @Transactional(readOnly = true)
    fun getPostComments(postId: Long, contestType: ContestTypeEnum, page: Int, size: Int): GetCommentResponseDto {
        val postCommentSlice: Slice<CommentEntity> = commentAdapter.getPostComments(postId, contestType, page, size)

        return GetCommentResponseDto.from(
            postId = postId,
            commentSlice = postCommentSlice
        )
    }

    @Transactional(readOnly = true)
    fun getCommentsReplies(contestType: ContestTypeEnum, parentCommentId: Long, page: Int, size: Int): GetCommentReplyResponseDto {
        val commentSlice: Slice<CommentEntity> = commentAdapter.getPostCommentsReplies(parentCommentId, contestType, page, size)

        return GetCommentReplyResponseDto.from(
            commentSlice = commentSlice
        )
    }

    @Transactional(readOnly = true)
    fun getMyPostComments(loginMember: MemberEntity, contestType: ContestTypeEnum, page: Int, size: Int): GetMyCommentResponseDto {
        val myCommentSlice: Slice<CommentEntity> = commentAdapter.getMyComments(loginMember, contestType, page, size)

        return GetMyCommentResponseDto.from(
            commentSlice = myCommentSlice
        )
    }

    @Transactional
    fun updateComment(loginMember: MemberEntity, request: CommentUpdateRequestDto) {
        val comment = commentValidator.checkMyComment(loginMember, request.commentId)

        commentAdapter.save(comment.copy(
            commentText = request.commentText
            )
        )
    }

    @Transactional
    fun deleteComment(loginMember: MemberEntity, commentId: Long) {
        val comment = commentValidator.checkMyComment(loginMember, commentId)

        commentAdapter.save(comment.copy(
            commentStatus = CommentStatusEnum.DELETE
            )
        )
    }
}
