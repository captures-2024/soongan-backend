package com.soongan.soonganbackend.soonganapi.unit.service.comment

import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request.CommentSaveRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request.CommentUpdateRequestDto
import com.soongan.soonganbackend.soonganapi.service.comment.CommentService
import com.soongan.soonganbackend.soonganapi.service.comment.validator.CommentValidator
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentStatusEnum
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.fcm.FcmTokenEntity
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soonganredis.producer.RedisMessageProducer
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.dto.Message
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.data.domain.SliceImpl
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CommentServiceTest {

    @MockK
    private lateinit var commentAdapter: CommentAdapter

    @MockK
    private lateinit var fcmTokenAdapter: FcmTokenAdapter

    @MockK
    private lateinit var weeklyContestPostAdapter: WeeklyContestPostAdapter

    @MockK
    private lateinit var commentValidator: CommentValidator

    @MockK
    private lateinit var redisMessageProducer: RedisMessageProducer

    @InjectMockKs
    private lateinit var commentService: CommentService

    @Test
    fun `댓글 저장 성공`() {
        // given
        val loginMember = MemberEntity(id = 1)
        val request = CommentSaveRequestDto(
            postId = 1L,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "test comment",
            parentCommentId = null
        )
        val post = WeeklyContestPostEntity(
            weeklyContest = WeeklyContestEntity(),
            member = loginMember
        )
        val savedComment = CommentEntity(
            member = loginMember,
            postId = request.postId,
            contestType = request.contestType,
            commentText = request.commentText,
            parentComment = null
        )
        val fcmTokens = listOf(FcmTokenEntity(
            token = "token",
            deviceId = "deviceId",
            deviceType = UserAgentEnum.ANDROID
        ))

        // mock
        every { weeklyContestPostAdapter.getByIdOrNull(request.postId) } returns post
        every { commentAdapter.save(any()) } returns savedComment
        every { weeklyContestPostAdapter.save(any()) } returns post.copy(commentCount = 1)
        every { fcmTokenAdapter.findAllByMemberId(any()) } returns fcmTokens
        every { redisMessageProducer.sendMessage(any(), any<Message>()) } returns Unit

        // when
        commentService.saveComment(loginMember, request)

        // then
        verify { commentAdapter.save(any()) }
        verify { weeklyContestPostAdapter.save(match { it.commentCount == 1 }) }
        verify { fcmTokenAdapter.findAllByMemberId(any()) }
        verify { redisMessageProducer.sendMessage(any(), any<Message>()) }
    }

    @Test
    fun `대댓글 저장 성공`() {
        // given
        val loginMember = MemberEntity(id = 1)
        val parentComment = CommentEntity(
            id = 1L,
            member = loginMember,
            postId = 1L,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "parent comment",
            parentComment = null
        )
        val request = CommentSaveRequestDto(
            postId = 1L,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "reply comment",
            parentCommentId = parentComment.id
        )
        val post = WeeklyContestPostEntity(
            weeklyContest = WeeklyContestEntity(),
            member = loginMember
        )
        val fcmTokens = listOf(FcmTokenEntity(
            token = "token",
            deviceId = "deviceId",
            deviceType = UserAgentEnum.ANDROID
        ))

        // mock
        every { weeklyContestPostAdapter.getByIdOrNull(request.postId) } returns post
        every { commentAdapter.getByIdOrNull(request.parentCommentId!!) } returns parentComment
        every { commentAdapter.save(any()) } returns parentComment
        every { weeklyContestPostAdapter.save(any()) } returns post.copy(commentCount = 2)
        every { fcmTokenAdapter.findAllByMemberId(any()) } returns fcmTokens
        every { redisMessageProducer.sendMessage(any(), any<Message>()) } returns Unit

        // when
        commentService.saveComment(loginMember, request)

        // then
        verify { commentAdapter.save(match {
            it.parentComment?.id == parentComment.id &&
                    it.commentText == request.commentText
        }) }
        verify { fcmTokenAdapter.findAllByMemberId(any()) }
        verify { redisMessageProducer.sendMessage(any(), any<Message>()) }
    }

    @Test
    fun `댓글 조회 성공`() {
        // given
        val postId = 1L
        val contestType = ContestTypeEnum.WEEKLY
        val page = 0
        val size = 10
        val comments = listOf(
            CommentEntity(
                id = 1L,
                member = MemberEntity(id = 1L, email = "test1@example.com", provider = ProviderEnum.GOOGLE),
                postId = postId,
                contestType = contestType,
                commentText = "comment 1",
                parentComment = null
            ),
            CommentEntity(
                id = 3L,
                member = MemberEntity(id = 2L, email = "test2@example.com", provider = ProviderEnum.GOOGLE),
                postId = postId,
                contestType = contestType,
                commentText = "reply comment",
                parentComment = CommentEntity(
                    id = 2L,
                    member = MemberEntity(id = 3L, email = "test3@example.com", provider = ProviderEnum.GOOGLE),
                    postId = postId,
                    contestType = contestType,
                    commentText = "comment 2",
                    parentComment = null
                )
            )
        )
        val slice = SliceImpl(comments)

        every {
            commentAdapter.getPostComments(postId, contestType, page, size)
        } returns slice

        // when
        val result = commentService.getPostComments(postId, contestType, page, size)

        // then
        assertThat(comments.size).isEqualTo(result.comments.size)
        assertThat(comments[0].commentText).isEqualTo(result.comments[0].commentText)
    }

    @Test
    fun `댓글 수정 성공`() {
        // given
        val loginMember = MemberEntity()
        val comment = CommentEntity(
            id = 1L,
            postId = 1L,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "original comment",
            member = loginMember,
            parentComment = null
        )
        val request = CommentUpdateRequestDto(
            contestType = ContestTypeEnum.WEEKLY,
            postId = comment.postId,
            commentId = comment.id!!,
            commentText = "updated comment"
        )

        every { commentValidator.checkMyComment(loginMember, request.commentId) } returns comment
        every { commentAdapter.save(any()) } returns comment.copy(commentText = request.commentText)

        // when
        commentService.updateComment(loginMember, request)

        // then
        verify { commentAdapter.save(match { it.commentText == request.commentText }) }
    }

    @Test
    fun `댓글 삭제 성공`() {
        // given
        val loginMember = MemberEntity()
        val comment = CommentEntity(
            id = 1L,
            postId = 1L,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "comment to delete",
            member = loginMember,
            parentComment = null
        )

        every { commentValidator.checkMyComment(loginMember, comment.id!!) } returns comment
        every { commentAdapter.save(any()) } returns comment.copy(commentStatus = CommentStatusEnum.DELETE)

        // when
        commentService.deleteComment(loginMember, comment.id!!)

        // then
        verify { commentAdapter.save(match { it.commentStatus == CommentStatusEnum.DELETE }) }
    }

    @Test
    fun `존재하지 않는 게시물에 댓글 작성 실패`() {
        // given
        val loginMember = MemberEntity()
        val request = CommentSaveRequestDto(
            postId = 999L,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "test comment",
            parentCommentId = null
        )

        every { weeklyContestPostAdapter.getByIdOrNull(request.postId) } returns null

        // when & then
        assertThatThrownBy { commentService.saveComment(loginMember, request) }
            .isInstanceOf(SoonganException::class.java)
            .extracting(SoonganException::statusCode.name)
            .isEqualTo(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)
    }

    @Test
    fun `내 댓글 조회 성공`() {
        // given
        val loginMember = MemberEntity()
        val contestType = ContestTypeEnum.WEEKLY
        val page = 0
        val size = 10
        val myComments = listOf(
            CommentEntity(
                id = 1L,
                postId = 1L,
                contestType = contestType,
                commentText = "my comment 1",
                member = loginMember,
                parentComment = null
            ),
            CommentEntity(
                id = 2L,
                postId = 2L,
                contestType = contestType,
                commentText = "my comment 2",
                member = loginMember,
                parentComment = null
            )
        )
        val slice = SliceImpl(myComments)

        every {
            commentAdapter.getMyComments(loginMember, contestType, page, size)
        } returns slice

        // when
        val result = commentService.getMyPostComments(loginMember, contestType, page, size)

        // then
        assertThat(myComments.size).isEqualTo(result.comments.size)
        assertThat(result.comments.all { it.commentText.startsWith("my comment")}).isTrue()
    }
}