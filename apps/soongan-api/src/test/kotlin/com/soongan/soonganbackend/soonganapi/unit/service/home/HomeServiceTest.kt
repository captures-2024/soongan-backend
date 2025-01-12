package com.soongan.soonganbackend.soonganapi.unit.service.home

import com.soongan.soonganbackend.soonganapi.service.home.HomeService
import com.soongan.soonganbackend.soonganapi.service.weeklyContest.validator.WeeklyContestValidator
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class HomeServiceTest {

    @MockK
    private lateinit var weeklyContestPostAdapter: WeeklyContestPostAdapter

    @MockK
    private lateinit var weeklyContestValidator: WeeklyContestValidator

    @InjectMockKs
    private lateinit var homeService: HomeService

    @Test
    fun `홈 화면 정보 조회 성공`() {
        // given
        val loginMember = MemberEntity()
        val weeklyContest = WeeklyContestEntity(
            id = 1,
            round = 1,
            subject = "test-subject",
            startAt = LocalDateTime.now().minusDays(1),
            endAt = LocalDateTime.now().plusDays(1),
        )
        val homeWeeklyContestPostList = listOf(
            WeeklyContestPostEntity(
                id = 1,
                member = loginMember,
                weeklyContest = weeklyContest,
                imageUrl = "test-image-url",
            ),
            WeeklyContestPostEntity(
                id = 2,
                member = loginMember,
                weeklyContest = weeklyContest,
            )
        )

        // mock
        every { weeklyContestValidator.getWeeklyContestIfValidRound() } returns weeklyContest
        every { weeklyContestPostAdapter.getAllWeeklyContestPostByMemberAndWeeklyContest(loginMember, weeklyContest) } returns homeWeeklyContestPostList

        // when
        val homeResponseDto = homeService.getHome(loginMember)

        // then
        assert(homeResponseDto.postInfo.size == homeWeeklyContestPostList.size)
        assertThat(homeResponseDto)
            .extracting("contestInfo.subject", "contestInfo.startAt", "contestInfo.endAt")
            .containsExactly(weeklyContest.subject, weeklyContest.startAt, weeklyContest.endAt)
    }
}