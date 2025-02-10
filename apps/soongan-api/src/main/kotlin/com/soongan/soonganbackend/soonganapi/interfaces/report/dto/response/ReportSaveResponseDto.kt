package com.soongan.soonganbackend.soonganapi.interfaces.report.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportEntity
import com.soongan.soonganbackend.soongansupport.domain.ReportTypeEnum

data class ReportSaveResponseDto(
    val id: Long,
    val reportMemberId: Long,
    val targetMemberId: Long,
    val targetId: Long,
    val targetType: String,
    val reportType: ReportTypeEnum,
    val reason: String?,
) {
    companion object {
        fun from(reportEntity: ReportEntity): ReportSaveResponseDto {
            return ReportSaveResponseDto(
                id = reportEntity.id!!,
                reportMemberId = reportEntity.reportMember.id!!,
                targetMemberId = reportEntity.targetMember.id!!,
                targetId = reportEntity.targetId,
                targetType = reportEntity.targetType.name,
                reportType = reportEntity.reportType,
                reason = reportEntity.reason,
            )
        }
    }
}