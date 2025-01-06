package com.soongan.soonganbackend.soonganapi.unit.service.report

import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.request.ReportSaveRequestDto
import com.soongan.soonganbackend.soonganapi.service.report.ReportService
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.domain.ReportTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
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
class ReportServiceTest {

    @MockK
    private lateinit var reportAdapter: ReportAdapter

    @MockK
    private lateinit var weeklyContestPostAdapter: WeeklyContestPostAdapter

    @MockK
    private lateinit var commentAdapter: CommentAdapter

    @InjectMockKs
    private lateinit var reportService: ReportService

    @Test
    fun `신고하기 성공`() {
        // given
        val loginMember = MemberEntity(id = 1)
        val request = ReportSaveRequestDto(
            targetId = 1,
            targetType = ReportTargetTypeEnum.WEEKLY_POST,
            reportType = ReportTypeEnum.SPAM,
            reason = "스팸입니다."
        )
        val targetMember = MemberEntity(id = 2)
        val post = WeeklyContestPostEntity(
            id = 1,
            weeklyContest = WeeklyContestEntity(),
            member = targetMember
        )
        val report = ReportEntity(
            id = 1,
            reportMember = loginMember,
            targetMember = targetMember,
            targetId = post.id!!,
            targetType = ReportTargetTypeEnum.WEEKLY_POST,
            reportType = ReportTypeEnum.SPAM,
            reason = request.reason
        )

        // mock
        every { weeklyContestPostAdapter.getByIdOrNull(any()) } returns post
        every { reportAdapter.save(any()) } returns report

        // when
        val result = reportService.report(loginMember, request)

        // then
        assertThat(result)
            .extracting(
                "id",
                "reportMemberId",
                "targetMemberId",
                "targetId",
                "targetType",
                "reportType",
                "reason"
            )
            .containsExactly(
                report.id,
                loginMember.id,
                targetMember.id,
                post.id,
                ReportTargetTypeEnum.WEEKLY_POST.name,
                ReportTypeEnum.SPAM.message,
                request.reason
            );
    }

    @Test
    fun `신고하기 실패 - 존재하지 않는 신고 대상`() {
        // given
        val loginMember = MemberEntity(id = 1)
        val request = ReportSaveRequestDto(
            targetId = 1,
            targetType = ReportTargetTypeEnum.WEEKLY_POST,
            reportType = ReportTypeEnum.SPAM,
            reason = "스팸입니다."
        )

        // mock
        every { weeklyContestPostAdapter.getByIdOrNull(any()) } returns null

        // when & then
        val exception = assertThrows<SoonganException> {
            reportService.report(loginMember, request)
        }
        assertThat(exception.statusCode).isEqualTo(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)
    }
}