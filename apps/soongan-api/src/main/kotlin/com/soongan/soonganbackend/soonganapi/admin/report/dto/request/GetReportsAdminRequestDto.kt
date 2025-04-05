package com.soongan.soonganbackend.soonganapi.admin.report.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

@Schema(description = "신고 내역 조회 요청 DTO")
data class GetReportsAdminRequestDto(
    @Schema(description = "신고자 이메일")
    val reporterEmail: String? = null,

    @Schema(description = "피신고자 이메일")
    val reportedEmail: String? = null,

    @Schema(description = "페이지")
    @field:NotNull(message = "페이지는 필수입니다.")
    @field:Min(0)
    val page: Int,

    @Schema(description = "페이지 사이즈")
    @field:NotNull(message = "페이지 사이즈는 필수입니다.")
    @field:Min(1)
    val size: Int = 10
)