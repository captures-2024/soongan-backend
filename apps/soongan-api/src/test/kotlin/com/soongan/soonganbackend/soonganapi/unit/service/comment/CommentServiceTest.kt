package com.soongan.soonganbackend.soonganapi.unit.service.comment

import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request.CommentSaveRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.comment.dto.request.CommentUpdateRequestDto
import com.soongan.soonganbackend.soonganapi.service.comment.CommentService
import com.soongan.soonganbackend.soonganapi.service.comment.validator.CommentValidator
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentStatusEnum
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.SliceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CommentServiceTest {

    @MockK
    private lateinit var commentAdapter: CommentAdapter

    @MockK
    private lateinit var weeklyContestPostAdapter: WeeklyContestPostAdapter

    @MockK
    private lateinit var commentValidator: CommentValidator

    @InjectMockKs
    private lateinit var commentService: CommentService

    @BeforeEach
    fun setUp() {
        commentAdapter = mockk()
        weeklyContestPostAdapter = mockk()
        commentValidator = mockk()
        commentService = CommentService(
            commentAdapter,
            weeklyContestPostAdapter,
            commentValidator
        )
    }

    @Test
    fun `댓글 저장 성공`() {
        // given
        val loginMember = MemberEntity(
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )
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

        // mock
        every { weeklyContestPostAdapter.getByIdOrNull(request.postId) } returns post
        every { commentAdapter.save(any()) } returns savedComment
        every { weeklyContestPostAdapter.save(any()) } returns post.copy(commentCount = 1)

        // when
        commentService.saveComment(loginMember, request)

        // then
        verify { commentAdapter.save(any()) }
        verify { weeklyContestPostAdapter.save(match { it.commentCount == 1 }) }
    }

    @Test
    fun `대댓글 저장 성공`() {
        // given
        val loginMember = MemberEntity(
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )
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

        // mock
        every { weeklyContestPostAdapter.getByIdOrNull(request.postId) } returns post
        every { commentAdapter.getByIdOrNull(request.parentCommentId!!) } returns parentComment
        every { commentAdapter.save(any()) } returns parentComment
        every { weeklyContestPostAdapter.save(any()) } returns post.copy(commentCount = 2)

        // when
        commentService.saveComment(loginMember, request)

        // then
        verify { commentAdapter.save(match {
            it.parentComment?.id == parentComment.id &&
                    it.commentText == request.commentText
        }) }
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
        assertEquals(comments.size, result.comments.size)
        assertEquals(comments[0].commentText, result.comments[0].commentText)
    }

    @Test
    fun `댓글 수정 성공`() {
        // given
        val loginMember = MemberEntity(
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )
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
        val loginMember = MemberEntity(
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )
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
        val loginMember = MemberEntity(
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )
        val request = CommentSaveRequestDto(
            postId = 999L,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "test comment",
            parentCommentId = null
        )

        every { weeklyContestPostAdapter.getByIdOrNull(request.postId) } returns null

        // when & then
        assertThrows<SoonganException> {
            commentService.saveComment(loginMember, request)
        }
    }

    @Test
    fun `내 댓글 조회 성공`() {
        // given
        val loginMember = MemberEntity(
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )
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
        assertEquals(myComments.size, result.comments.size)
        assertTrue(result.comments.all { it.commentText.startsWith("my comment") })
    }
}