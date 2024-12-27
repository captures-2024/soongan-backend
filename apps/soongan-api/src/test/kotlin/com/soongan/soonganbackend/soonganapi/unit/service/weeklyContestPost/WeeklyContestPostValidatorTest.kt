package com.soongan.soonganbackend.soonganapi.unit.service.weeklyContestPost

import com.soongan.soonganbackend.soonganapi.service.weeklyContestPost.WeeklyContestPostValidator
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class WeeklyContestPostValidatorTest {

    @MockK
    private lateinit var weeklyContestPostAdapter: WeeklyContestPostAdapter

    @InjectMockKs
    private lateinit var validator: WeeklyContestPostValidator


    @Test
    @DisplayName("최대 게시물 등록 개수 제한 검증 성공/실패")
    fun validateMaxRegisterPost() {
        // given
        val member = MemberEntity(
            email = "email",
            provider = ProviderEnum.APPLE
        )

        val weeklyContest = WeeklyContestEntity(
            round = 1,
            maxPostAllowed = 3
        )

        val registeredPostCountByMember = 2

        every { weeklyContestPostAdapter.countRegisteredPostByMember(weeklyContest, member) } returns registeredPostCountByMember

        // then
        assertThatThrownBy { validator.validateMaxRegisterPost(weeklyContest, member) }
            .isInstanceOf(SoonganException::class.java)
            .extracting(SoonganException::statusCode.name)
            .isEqualTo(StatusCode.SOONGAN_API_WEEKLY_CONTEST_POST_REGISTER_LIMIT_EXCEEDED)
    }

    @Test
    @DisplayName("게시물 작성자 검증 성공/실패")
    fun validatePostOwner() {
        // given
        val member = MemberEntity(
            email = "email1",
            provider = ProviderEnum.APPLE
        )

        val wrongMember = MemberEntity(
            email = "email2",
            provider = ProviderEnum.GOOGLE
        )

        val weeklyContest = WeeklyContestEntity(
            round = 1,
        )

        val postId = 1L

        val post = WeeklyContestPostEntity(
            member = member,
            weeklyContest = weeklyContest
        )

        every { weeklyContestPostAdapter.getByIdOrNull(postId) } returns post

        // when
        validator.validatePostOwner(member, postId)

        // then
        verify(exactly = 1) { weeklyContestPostAdapter.getByIdOrNull(postId) }

        assertThatThrownBy { validator.validatePostOwner(wrongMember, postId) }
            .isInstanceOf(SoonganException::class.java)
            .extracting(SoonganException::statusCode.name)
            .isEqualTo(StatusCode.SOONGAN_API_NOT_OWNER_WEEKLY_CONTEST_POST)
    }
}
