package com.soongan.soonganbackend.soonganpersistence.storage.report

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository: JpaRepository<ReportEntity, Long> {
    fun findByReportMember(reportMember: MemberEntity): List<ReportEntity>
}