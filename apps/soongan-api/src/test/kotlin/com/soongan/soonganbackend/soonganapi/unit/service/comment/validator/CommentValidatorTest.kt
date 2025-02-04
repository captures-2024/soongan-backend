package com.soongan.soonganbackend.soonganapi.unit.service.comment.validator

import com.soongan.soonganbackend.soonganapi.service.comment.validator.CommentValidator
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
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
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CommentValidatorTest {

    @MockK
    private lateinit var commentAdapter: CommentAdapter

    @InjectMockKs
    private lateinit var commentValidator: CommentValidator

    @Test
    fun `본인이 작성한 댓글인지 확인 성공`() {
        // given
        val loginMember = MemberEntity(
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )
        val comment = CommentEntity(
            postId = 1,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "test comment",
            member = loginMember,
            parentComment = null
        )

        // mock
        every { commentAdapter.getByIdOrNull(any()) } returns comment

        // when
        val result = commentValidator.checkMyComment(loginMember, 1)

        // then
        assertThat(result).isEqualTo(comment)
        verify { commentAdapter.getByIdOrNull(any()) }
    }

    @Test
    fun `본인이 작성한 댓글인지 확인 실패 - 존재하지 않는 댓글`() {
        // given
        val loginMember = MemberEntity(
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )

        // mock
        every { commentAdapter.getByIdOrNull(any()) } returns null

        // when, then
        assertThatThrownBy { commentValidator.checkMyComment(loginMember, 1) }
            .isInstanceOf(SoonganException::class.java)
            .extracting(SoonganException::statusCode.name)
            .isEqualTo(StatusCode.SOONGAN_API_NOT_FOUND_COMMENT)
    }

    @Test
    fun `본인이 작성한 댓글인지 확인 실패 - 다른 사용자가 작성한 댓글`() {
        // given
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE
        )
        val comment = CommentEntity(
            postId = 1,
            contestType = ContestTypeEnum.WEEKLY,
            commentText = "test comment",
            member = MemberEntity(
                id = 2,
                email = "test2@example.com",
                provider = ProviderEnum.GOOGLE
            ),
            parentComment = null
        )

        // mock
        every { commentAdapter.getByIdOrNull(any()) } returns comment

        // when, then
        assertThatThrownBy { commentValidator.checkMyComment(loginMember, 1) }
            .isInstanceOf(SoonganException::class.java)
            .extracting(SoonganException::statusCode.name)
            .isEqualTo(StatusCode.SOONGAN_API_NOT_OWNER_COMMENT)
    }
}