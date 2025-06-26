package com.soongan.soonganbackend.soonganapi.interfaces.report.dto.request

import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(description = "피신고자 소명 DTO")
data class ExplainSaveRequestDto(
    @Schema(description = "신고 대상 ID", required = true)
    @field:NotNull
    val targetId: Long,

    @Schema(description = "신고 대상 타입 (post/comment", required = true)
    @field:NotNull
    val targetType: ReportTargetTypeEnum,

    @Schema(description = "소명 내용", required = true)
    val explain: String
)
