package com.soongan.soonganbackend.soonganapi.admin.report

import com.soongan.soonganbackend.soonganapi.admin.report.dto.request.GetReportsAdminRequestDto
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.ADMIN + Uri.REPORT)
@Tag(name = "Report Admin Apis", description = "신고 관련 Admin API")
class ReportAdminController {

    @Operation(
        summary = "신고 내역 조회 Api",
        description = "신고 내역을 조회합니다. 신고자 혹은 피신고자 이메일로 조회할 수도 있습니다.",
    )
    @GetMapping
    fun getAll(@ModelAttribute @Valid requestDto: GetReportsAdminRequestDto): List<Int> {
        return listOf(1, 2, 3)
    }
}