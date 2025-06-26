package com.soongan.soonganbackend.soonganapi.interfaces.report

import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.request.ExplainSaveRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.request.ReportSaveRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.response.ReportSaveResponseDto
import com.soongan.soonganbackend.soonganapi.service.report.ReportService
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.REPORT)
@Tag(name = "Report Apis", description = "신고 관리 API")
class ReportController(
    private val reportService: ReportService
) {

    @Operation(
        summary = "신고하기 Api",
        description = "게시글, 댓글을 신고합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    @PostMapping
    fun report(@LoginMember loginMember: MemberEntity, @RequestBody @Valid reportSaveRequestDto: ReportSaveRequestDto): ReportSaveResponseDto {
        return reportService.report(loginMember, reportSaveRequestDto)
    }

    @Operation(
        summary = "소명하기 API",
        description = "피신고자가 소명 사유를 입력하는 API 입니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    @PostMapping
    fun explain(@LoginMember loginMember: MemberEntity, @RequestBody @Valid explainSaveRequestDto: ExplainSaveRequestDto) {
        reportService.explain(loginMember, explainSaveRequestDto)
    }
}