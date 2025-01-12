package com.soongan.soonganbackend.soonganapi.service.report

import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.request.ReportSaveRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.response.ReportSaveResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service

@Service
class ReportService(
    private val reportAdapter: ReportAdapter,
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val commentAdapter: CommentAdapter
) {

    fun report(loginMember: MemberEntity, reportSaveRequestDto: ReportSaveRequestDto): ReportSaveResponseDto {
        val targetMember = when (reportSaveRequestDto.targetType) {
            ReportTargetTypeEnum.WEEKLY_POST -> {
                weeklyContestPostAdapter.getByIdOrNull(reportSaveRequestDto.targetId)?.member ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)
            }
            ReportTargetTypeEnum.DAILY_POST -> {
                throw SoonganException(StatusCode.SOONGAN_API_INVALID_REQUEST, "일간 콘테스트 게시글은 아직 지원하지 않습니다.")
            }
            ReportTargetTypeEnum.COMMENT -> {
                commentAdapter.getByIdOrNull(reportSaveRequestDto.targetId)?.member ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_COMMENT)
            }
        }

        val savedReport = reportAdapter.save(
            ReportEntity(
                reportMember = loginMember,
                targetMember = targetMember,
                targetId = reportSaveRequestDto.targetId,
                targetType = reportSaveRequestDto.targetType,
                reportType = reportSaveRequestDto.reportType,
                reason = reportSaveRequestDto.reason,
            )
        )

        return ReportSaveResponseDto.from(savedReport)
    }
}