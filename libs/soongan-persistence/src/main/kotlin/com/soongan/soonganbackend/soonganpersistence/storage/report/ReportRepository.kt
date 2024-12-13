package com.soongan.soonganbackend.soonganpersistence.storage.report

import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository: JpaRepository<ReportEntity, Long>