package com.soongan.soonganbackend.soonganapi.admin.member

import com.soongan.soonganbackend.soonganapi.admin.member.dto.request.GetMembersAdminRequestDto
import com.soongan.soonganbackend.soonganapi.admin.member.dto.response.MemberAdminResponseDto
import com.soongan.soonganbackend.soonganapi.service.member.MemberAdminService
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.ADMIN + Uri.MEMBERS)
@Tag(name = "Member Admin Apis", description = "회원 관련 Admin API")
class MemberAdminController(
    private val memberAdminService: MemberAdminService
) {

    @Operation(
        summary = "회원 조회",
        description = "회원 정보를 조회합니다."
    )
    @GetMapping
    fun getAll(@ModelAttribute() requestDto: GetMembersAdminRequestDto): List<MemberAdminResponseDto> {
        return memberAdminService.getAll(requestDto)
    }

    @Operation(
        summary = "탈퇴 회원 재가입 제한 해제",
        description = "탈퇴한 회원 데이터를 완전히 삭제하여 재가입 제한을 해제합니다. 단, 회원의 모든 데이터가 삭제되며, 다시 가입해야 합니다."
    )
    @DeleteMapping("/{memberId:[0-9]+}")
    fun deleteOne(@PathVariable memberId: Long): Unit {
        memberAdminService.deleteOne(memberId)
    }
}