package com.soongan.soonganbackend.soonganapi.admin.member

import com.soongan.soonganbackend.soonganapi.admin.member.dto.request.GetMembersAdminRequestDto
import com.soongan.soonganbackend.soonganapi.admin.member.dto.response.MemberAdminResponseDto
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.ADMIN + Uri.MEMBERS)
@Tag(name = "Member Admin Apis", description = "회원 관련 Admin API")
class MemberAdminController {

    @Operation(
        summary = "회원 조회",
        description = "회원 정보를 조회합니다."
    )
    @GetMapping
    fun getAll(@ModelAttribute() requestDto: GetMembersAdminRequestDto): List<MemberAdminResponseDto> {
        println(requestDto)
        return emptyList()
    }
}