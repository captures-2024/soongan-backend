package com.soongan.soonganbackend.soonganpersistence.storage.report

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
}