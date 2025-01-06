package com.soongan.soonganbackend.soonganapi.unit.service.weeklyContest.validator

import com.soongan.soonganbackend.soonganapi.service.weeklyContest.validator.WeeklyContestValidator
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class WeeklyContestValidatorTest {

    @MockK
    private lateinit var weeklyContestAdapter: WeeklyContestAdapter

    @InjectMockKs
    private lateinit var weeklyContestValidator: WeeklyContestValidator

    @Test
    fun `라운드로 콘테스트 조회 성공`() {
        // given
        val round = 1
        val contest = WeeklyContestEntity(
            round = round
        )

        // mock
        every { weeklyContestAdapter.getWeeklyContest(round) } returns contest

        // when
        val result = weeklyContestValidator.getWeeklyContestIfValidRound(round)

        // then
        assertThat(result.round).isEqualTo(round)
    }

    @Test
    fun `라운드로 콘테스트 조회 실패 - 존재하지 않는 콘테스트`() {
        // given
        val round = 1

        // mock
        every { weeklyContestAdapter.getWeeklyContest(round) } returns null

        // when
        val exception = assertThrows<SoonganException> {
            weeklyContestValidator.getWeeklyContestIfValidRound(round)
        }
        assertThat(exception.statusCode).isEqualTo(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST)
    }

    @Test
    fun `라운드가 주어지지 않으면 현재 진행 중인 콘테스트 조회 성공`() {
        // given
        val contest = WeeklyContestEntity(
            round = 1
        )

        // mock
        every { weeklyContestAdapter.getInProgressWeeklyContest(any()) } returns contest

        // when
        val result = weeklyContestValidator.getWeeklyContestIfValidRound(null)

        // then
        assertThat(result.round).isEqualTo(1)
    }

    @Test
    fun `가장 마지막 콘테스트 조회`() {
        // given
        val contest = WeeklyContestEntity(
            round = 1
        )

        // mock
        every { weeklyContestAdapter.getInProgressWeeklyContest(any()) } returns null
        every { weeklyContestAdapter.getLatestEndedWeeklyContest(any()) } returns contest

        // when
        val result = weeklyContestValidator.getWeeklyContestIfValidRound(null)

        // then
        assertThat(result.round).isEqualTo(1)
    }
}