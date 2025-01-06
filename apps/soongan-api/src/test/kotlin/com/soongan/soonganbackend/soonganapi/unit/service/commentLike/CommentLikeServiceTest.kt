package com.soongan.soonganbackend.soonganapi.unit.service.commentLike

import com.soongan.soonganbackend.soonganapi.interfaces.commentLike.dto.request.CommentLikeRequestDto
import com.soongan.soonganbackend.soonganapi.service.commentLike.CommentLikeService
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.commentLike.CommentLikeAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CommentLikeServiceTest {

    @MockK
    private lateinit var commentLikeAdapter: CommentLikeAdapter

    @MockK
    private lateinit var commentAdapter: CommentAdapter

    @InjectMockKs
    private lateinit var commentLikeService: CommentLikeService

    @Test
    fun `좋아요 성공`() {
        // given
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )
        val request = CommentLikeRequestDto(
            commentId = 1,
            contestTypeEnum = ContestTypeEnum.WEEKLY
        )
        val comment = CommentEntity(
            id = request.commentId,
            member = loginMember,
            postId = 1,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "test",
            parentComment = null,
            likeCount = 3
        )

        // mock
        every { commentAdapter.getByIdOrNull(request.commentId) } returns comment
        every { commentLikeAdapter.existsByCommentIdAndContestTypeAndMember(comment.id!!, request.contestTypeEnum, loginMember) } returns false
        every { commentLikeAdapter.addLike(comment.id!!, request.contestTypeEnum, loginMember) } returns mockk()
        every { commentAdapter.save(comment.copy(likeCount = comment.likeCount + 1)) } returns comment.copy(likeCount = comment.likeCount + 1)

        // when
        val result = commentLikeService.addLike(loginMember, request)

        // then
        assert(result.commentId == comment.id)
        assert(result.likeCount == comment.likeCount + 1)
    }

    @Test
    fun `좋아요 실패 - 존재하지 않는 댓글`() {
        // given
        val request = CommentLikeRequestDto(
            commentId = 1,
            contestTypeEnum = ContestTypeEnum.WEEKLY
        )

        // mock
        every { commentAdapter.getByIdOrNull(request.commentId) } returns null

        // when, then
        val exception = assertThrows<SoonganException> {
            commentLikeService.addLike(mockk(), request)
        }
        assert(exception.statusCode == StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_COMMENT)
    }

    @Test
    fun `좋아요 실패 - 중복 좋아요`() {
        // given
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )
        val request = CommentLikeRequestDto(
            commentId = 1,
            contestTypeEnum = ContestTypeEnum.WEEKLY
        )
        val comment = CommentEntity(
            id = request.commentId,
            member = loginMember,
            postId = 1,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "test",
            parentComment = null,
            likeCount = 3
        )

        // mock
        every { commentAdapter.getByIdOrNull(request.commentId) } returns comment
        every {
            commentLikeAdapter.existsByCommentIdAndContestTypeAndMember(
                comment.id!!,
                request.contestTypeEnum,
                loginMember
            )
        } returns true

        // when, then
        val exception = assertThrows<SoonganException> {
            commentLikeService.addLike(loginMember, request)
        }
        assert(exception.statusCode == StatusCode.SOONGAN_API_DUPLICATED_LIKE)
    }

    @Test
    fun `좋아요 취소 성공`() {
        // given
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )
        val request = CommentLikeRequestDto(
            commentId = 1,
            contestTypeEnum = ContestTypeEnum.WEEKLY
        )
        val comment = CommentEntity(
            id = request.commentId,
            member = loginMember,
            postId = 1,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "test",
            parentComment = null,
            likeCount = 3
        )

        // mock
        every { commentAdapter.getByIdOrNull(request.commentId) } returns comment
        every { commentLikeAdapter.cancelLike(comment.id!!, request.contestTypeEnum, loginMember) } returns Unit
        every { commentAdapter.save(comment.copy(likeCount = comment.likeCount - 1)) } returns comment.copy(likeCount = comment.likeCount - 1)

        // when
        val result = commentLikeService.cancelLike(loginMember, request)

        // then
        assert(result.commentId == comment.id)
        assert(result.likeCount == comment.likeCount - 1)
    }

    @Test
    fun `좋아요 취소 실패 - 존재하지 않는 댓글`() {
        // given
        val request = CommentLikeRequestDto(
            commentId = 1,
            contestTypeEnum = ContestTypeEnum.WEEKLY
        )

        // mock
        every { commentAdapter.getByIdOrNull(request.commentId) } returns null

        // when, then
        val exception = assertThrows<SoonganException> {
            commentLikeService.cancelLike(mockk(), request)
        }
        assert(exception.statusCode == StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_COMMENT)
    }
}