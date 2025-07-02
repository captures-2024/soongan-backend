package com.soongan.soonganbackend.soonganpersistence.storage.report

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReportAdapter(
    private val reportRepository: ReportRepository
) {

    @Transactional
    fun save(reportEntity: ReportEntity): ReportEntity {
        return reportRepository.save(reportEntity)
    }

    @Transactional(readOnly = true)
    fun getReportHistoriesByReportMember(loginMember: MemberEntity): List<ReportEntity> {
        return reportRepository.findByReportMember(loginMember)
    }

    @Transactional(readOnly = true)
    fun countByTargetIdAndTargetType(targetId: Long, targetType: ReportTargetTypeEnum): Long {
        return reportRepository.countByTargetIdAndTargetType(targetId, targetType)
    }
}