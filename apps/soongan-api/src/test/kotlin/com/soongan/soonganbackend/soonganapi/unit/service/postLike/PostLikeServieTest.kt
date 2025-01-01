package com.soongan.soonganbackend.soonganapi.unit.service.postLike

import com.soongan.soonganbackend.soonganapi.interfaces.postLike.dto.request.PostLikeRequestDto
import com.soongan.soonganbackend.soonganapi.service.postLike.PostLikeService
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.postLike.PostLikeAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.postLike.PostLikeEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
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
class PostLikeServieTest {

    @MockK
    private lateinit var weeklyContestPostAdapter: WeeklyContestPostAdapter

    @MockK
    private lateinit var postLikeAdapter: PostLikeAdapter

    @InjectMockKs
    private lateinit var postLikeService: PostLikeService

    @BeforeEach
    fun setUp() {
        weeklyContestPostAdapter = mockk()
        postLikeAdapter = mockk()
        postLikeService = PostLikeService(weeklyContestPostAdapter, postLikeAdapter)
    }

    @Test
    fun `좋아요 성공`() {
        // given
        val contestType = ContestTypeEnum.WEEKLY
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
        )
        val request = PostLikeRequestDto(
            postId = 1,
            contestType = contestType
        )
        val post = WeeklyContestPostEntity(
            id = 1,
            weeklyContest = WeeklyContestEntity(
                id = 1,
                round = 1,
                subject = "test-subject",
            ),
            member = loginMember
        )

        // mock
        every { weeklyContestPostAdapter.getByIdOrNull(any()) } returns post
        every { postLikeAdapter.existsByPostIdAndContestTypeAndMember(any(), any(), any()) } returns false
        every { postLikeAdapter.addLike(any(), any(), any()) } returns PostLikeEntity(
            id = 1,
            postId = post.id!!,
            contestType = contestType,
            member = loginMember
        )
        every { weeklyContestPostAdapter.save(any()) } returns post.copy(likeCount = post.likeCount + 1)

        // when
        val result = postLikeService.addLike(loginMember, request)

        // then
        assert(result.postId == post.id)
        assert(result.likeCount == post.likeCount + 1)
    }

    @Test
    fun `좋아요 실패 - 존재하지 않는 게시글`() {
        // given
        val contestType = ContestTypeEnum.WEEKLY
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
        )
        val request = PostLikeRequestDto(
            postId = 1,
            contestType = contestType
        )

        // mock
        every { weeklyContestPostAdapter.getByIdOrNull(any()) } returns null

        // when & then
        val exception = assertThrows<SoonganException> {
            postLikeService.addLike(loginMember, request)
        }
        assert(exception.statusCode == StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)
    }

    @Test
    fun `좋아요 실패 - 중복 좋아요`() {
        // given
        val contestType = ContestTypeEnum.WEEKLY
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
        )
        val request = PostLikeRequestDto(
            postId = 1,
            contestType = contestType
        )
        val post = WeeklyContestPostEntity(
            id = 1,
            weeklyContest = WeeklyContestEntity(
                id = 1,
                round = 1,
                subject = "test-subject",
            ),
            member = loginMember
        )

        // mock
        every { weeklyContestPostAdapter.getByIdOrNull(any()) } returns post
        every { postLikeAdapter.existsByPostIdAndContestTypeAndMember(any(), any(), any()) } returns true

        // when & then
        val exception = assertThrows<SoonganException> {
            postLikeService.addLike(loginMember, request)
        }
        assert(exception.statusCode == StatusCode.SOONGAN_API_DUPLICATED_LIKE)
    }

    @Test
    fun `좋아요 취소 성공`() {
        // given
        val contestType = ContestTypeEnum.WEEKLY
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
        )
        val request = PostLikeRequestDto(
            postId = 1,
            contestType = contestType
        )
        val post = WeeklyContestPostEntity(
            id = 1,
            weeklyContest = WeeklyContestEntity(
                id = 1,
                round = 1,
                subject = "test-subject",
            ),
            member = loginMember
        )

        // mock
        every { weeklyContestPostAdapter.getByIdOrNull(any()) } returns post
        every { postLikeAdapter.cancelLike(any(), any(), any()) } returns Unit
        every { weeklyContestPostAdapter.save(any()) } returns post.copy(likeCount = post.likeCount - 1)

        // when
        val result = postLikeService.cancelLike(loginMember, request)

        // then
        assert(result.postId == post.id)
        assert(result.likeCount == post.likeCount - 1)
    }

    @Test
    fun `좋아요 취소 실패 - 존재하지 않는 게시글`() {
        // given
        val contestType = ContestTypeEnum.WEEKLY
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
        )
        val request = PostLikeRequestDto(
            postId = 1,
            contestType = contestType
        )

        // mock
        every { weeklyContestPostAdapter.getByIdOrNull(any()) } returns null

        // when & then
        val exception = assertThrows<SoonganException> {
            postLikeService.cancelLike(loginMember, request)
        }
        assert(exception.statusCode == StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)
    }
}