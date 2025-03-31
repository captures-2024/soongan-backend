package com.soongan.soonganbackend.soonganapi.interfaces.report.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportEntity
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "유저 신고 내역 응답 DTO")
data class ReportHistoryResponseDto(
    @Schema(description = "신고 ID", required = true)
    val id: Long,

    @Schema(description = "신고 대상 ID", required = true)
    val targetId: Long,

    @Schema(description = "신고 대상 타입 (post/comment)", required = true)
    val targetType: String,
) {
    companion object {
        fun from(report: ReportEntity): ReportHistoryResponseDto {
            return ReportHistoryResponseDto(
                id = report.id!!,
                targetId = report.targetId,
                targetType = report.targetType.name,
            )
        }
    }
}