package com.soongan.soonganbackend.soonganpersistence.storage.report

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository: JpaRepository<ReportEntity, Long> {
    fun findByReportMember(reportMember: MemberEntity): List<ReportEntity>

    fun countByTargetIdAndTargetType(targetId: Long, targetType: ReportTargetTypeEnum): Long
}