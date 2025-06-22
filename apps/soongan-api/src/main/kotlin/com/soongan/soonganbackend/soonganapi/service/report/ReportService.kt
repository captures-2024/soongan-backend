package com.soongan.soonganbackend.soonganapi.service.report

import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.request.ReportSaveRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.response.ReportSaveResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.comment.CommentEntity
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ReportService(
    private val reportAdapter: ReportAdapter,
    private val weeklyContestPostAdapter: WeeklyContestPostAdapter,
    private val commentAdapter: CommentAdapter
) {

    fun report(loginMember: MemberEntity, dto: ReportSaveRequestDto): ReportSaveResponseDto {
        val target = getTargetEntity(dto.targetType, dto.targetId)
        val targetMember = getTargetMember(target)

        val savedReport = reportAdapter.save(
            ReportEntity(
                reportMember = loginMember,
                targetMember = targetMember,
                targetId = dto.targetId,
                targetType = dto.targetType,
                reportType = dto.reportType,
                reason = dto.reason,
            )
        )

        handleBlindingIfNeeded(dto.targetId, dto.targetType, target)

        val reportHistories = reportAdapter.getReportHistoriesByReportMember(loginMember)
        return ReportSaveResponseDto.from(savedReport, reportHistories)
    }

    private fun getTargetEntity(type: ReportTargetTypeEnum, id: Long): Any {
        return when (type) {
            ReportTargetTypeEnum.WEEKLY_POST ->
                weeklyContestPostAdapter.getByIdOrNull(id)
                    ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST_POST)

            ReportTargetTypeEnum.COMMENT ->
                commentAdapter.getByIdOrNull(id)
                    ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_COMMENT)

            ReportTargetTypeEnum.DAILY_POST ->
                throw SoonganException(StatusCode.SOONGAN_API_INVALID_REQUEST, "일간 콘테스트 게시글은 아직 지원하지 않습니다.")
        }
    }

    private fun getTargetMember(target: Any): MemberEntity {
        return when (target) {
            is WeeklyContestPostEntity -> target.member
            is CommentEntity -> target.member
            else -> throw SoonganException(StatusCode.SOONGAN_API_INVALID_REQUEST, "지원하지 않는 대상입니다.")
        }
    }

    private fun handleBlindingIfNeeded(targetId: Long, targetType: ReportTargetTypeEnum, target: Any): Unit {
        val reportCount = reportAdapter.countByTargetIdAndTargetType(targetId, targetType)
        if (reportCount < 3) return

        val now = LocalDateTime.now()
        when (target) {
            is WeeklyContestPostEntity -> {
                if (target.blindedAt == null) {
                    weeklyContestPostAdapter.save(target.copy(blindedAt = now))
                }
            }
            is CommentEntity -> {
                if (target.blindedAt == null) {
                    commentAdapter.save(target.copy(blindedAt = now))
                }
            }
        }
    }
}
