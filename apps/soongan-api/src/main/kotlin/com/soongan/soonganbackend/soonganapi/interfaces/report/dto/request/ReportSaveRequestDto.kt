package com.soongan.soonganbackend.soonganapi.interfaces.report.dto.request

import com.soongan.soonganbackend.soongansupport.domain.ReportTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "게시글 또는 댓글 신고 요청 DTO")
data class ReportSaveRequestDto(
    @Schema(description = "신고 대상 ID", required = true)
    @field:NotBlank
    val targetId: Long,

    @Schema(description = "신고 대상 타입 (post/comment)", required = true)
    @field:NotBlank
    val targetType: ReportTargetTypeEnum,

    @Schema(description = "신고 사유 타입", required = true)
    @field:NotBlank
    val reportType: ReportTypeEnum,

    @Schema(description = "신고 사유", required = false)
    val reason: String? = null
)
