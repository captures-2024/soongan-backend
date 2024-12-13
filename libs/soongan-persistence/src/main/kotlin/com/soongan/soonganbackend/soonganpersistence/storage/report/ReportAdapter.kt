package com.soongan.soonganbackend.soonganpersistence.storage.report

import org.springframework.stereotype.Component

@Component
class ReportAdapter(
    private val reportRepository: ReportRepository
)