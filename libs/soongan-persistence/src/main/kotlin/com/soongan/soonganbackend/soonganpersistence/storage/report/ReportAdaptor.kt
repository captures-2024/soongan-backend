package com.soongan.soonganbackend.soonganpersistence.storage.report

import org.springframework.stereotype.Component

@Component
class ReportAdaptor(
    private val reportRepository: ReportRepository
)