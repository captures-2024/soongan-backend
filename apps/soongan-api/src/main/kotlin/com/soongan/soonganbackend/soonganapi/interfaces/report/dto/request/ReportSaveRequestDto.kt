package com.soongan.soonganbackend.soonganapi.interfaces.report.dto.request

import com.soongan.soonganbackend.soongansupport.domain.ReportTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import jakarta.validation.constraints.NotNull

data class ReportSaveRequestDto(
    @field:NotNull
    val targetId: Long,

    @field:NotNull
    val targetType: ReportTargetTypeEnum,

    @field:NotNull
    val reportType: ReportTypeEnum,

    val reason: String? = null
)
