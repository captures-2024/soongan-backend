package com.soongan.soonganbackend.soonganapi.interfaces.report.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportEntity
import com.soongan.soonganbackend.soongansupport.domain.ReportTypeEnum
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "신고 저장 응답 DTO")
data class ReportSaveResponseDto(
    @Schema(description = "저장된 신고 ID", required = true)
    val id: Long,

    @Schema(description = "신고한 회원 ID", required = true)
    val reportMemberId: Long,

    @Schema(description = "신고 대상 회원 ID", required = true)
    val targetMemberId: Long,

    @Schema(description = "신고 대상 ID", required = true)
    val targetId: Long,

    @Schema(description = "신고 대상 타입 (post/comment)", required = true)
    val targetType: String,

    @Schema(description = "신고 사유 타입", required = true)
    val reportType: ReportTypeEnum,

    @Schema(description = "신고 사유", required = false)
    val reason: String?,

    @Schema(description = "유저가 신고한 내역", required = true)
    val reportHistories: List<ReportHistoryResponseDto>
) {
    companion object {
        fun from(reportEntity: ReportEntity, reportHistories: List<ReportEntity>): ReportSaveResponseDto {
            return ReportSaveResponseDto(
                id = reportEntity.id!!,
                reportMemberId = reportEntity.reportMember.id!!,
                targetMemberId = reportEntity.targetMember.id!!,
                targetId = reportEntity.targetId,
                targetType = reportEntity.targetType.name,
                reportType = reportEntity.reportType,
                reason = reportEntity.reason,
                reportHistories = reportHistories.map { reportHistory ->
                    ReportHistoryResponseDto.from(reportHistory)
                }
            )
        }
    }
}