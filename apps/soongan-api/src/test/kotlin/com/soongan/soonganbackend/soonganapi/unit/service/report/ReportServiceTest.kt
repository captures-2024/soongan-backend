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
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import com.soongan.soonganbackend.soongansupport.domain.ReportTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ReportServiceTest {
    private lateinit var reportAdapter: ReportAdapter
    private lateinit var weeklyContestPostAdapter: WeeklyContestPostAdapter
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var reportService: ReportService

    @BeforeEach
    fun setUp() {
        reportAdapter = mockk()
        weeklyContestPostAdapter = mockk()
        commentAdapter = mockk()
        reportService = ReportService(reportAdapter, weeklyContestPostAdapter, commentAdapter)
    }

    @Test
    fun `신고하기 성공`() {
        // given
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
        )
        val request = ReportSaveRequestDto(
            targetId = 1,
            targetType = ReportTargetTypeEnum.WEEKLY_POST,
            reportType = ReportTypeEnum.SPAM,
            reason = "스팸입니다."
        )
        val targetMember = MemberEntity(
            id = 2,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
        )
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
        assert(result.id == report.id)
        assert(result.reportMemberId == loginMember.id)
        assert(result.targetMemberId == targetMember.id)
        assert(result.targetId == post.id)
        assert(result.targetType == ReportTargetTypeEnum.WEEKLY_POST.name)
        assert(result.reportType == ReportTypeEnum.SPAM.message)
        assert(result.reason == request.reason)
    }

    @Test
    fun `신고하기 실패 - 존재하지 않는 신고 대상`() {
        // given
        val loginMember = MemberEntity(
            id = 1,
            email = "test@example.com",
            provider = ProviderEnum.GOOGLE,
        )
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
        assert(exception.statusCode == StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)
    }
}