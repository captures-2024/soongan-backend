package com.soongan.soonganbackend.soonganapi.interfaces.member

import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.request.UpdateProfileRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response.*
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganapi.service.member.MemberService
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.MEMBERS)
@Tag(name = "Member Apis", description = "회원 관리 API")
class MemberController(
    private val memberService: MemberService
) {

    @Operation(summary = "회원 정보 조회 Api", description = "JWT를 읽어 로그인한 회원의 정보를 조회합니다.")
    @GetMapping
    fun getUserinfo(@LoginMember loginMember: MemberEntity): MemberInfoResponseDto {
        return memberService.getMemberInfo(loginMember)
    }

    @Operation(summary = "닉네임 중복 확인 Api", description = "닉네임이 중복되는지 확인합니다. true면 사용 가능, false면 중복.")
    @GetMapping(Uri.CHECK_NICKNAME)
    fun checkNickname(@RequestParam nickname: String): Boolean {
        return memberService.checkNickname(nickname)
    }

    @Operation(summary = "출생 연도 변경 Api", description = "출생 연도를 변경합니다.")
    @PatchMapping(Uri.BIRTH_YEAR)
    fun updateBirthYear(
        @LoginMember loginMember: MemberEntity,
        @RequestParam birthYear: Int
    ): UpdateBirthDateResponseDto {
        return memberService.updateBirthDate(loginMember, birthYear)
    }

    @Operation(summary = "프로필 변경 Api", description = "프로필 사진, 닉네임, 자기소개 등 프로필 정보를 변경합니다.")
    @PatchMapping(Uri.PROFILE)
    fun updateProfile(@LoginMember loginMember: MemberEntity, @ModelAttribute request: UpdateProfileRequestDto): UpdateProfileResponseDto {
        return memberService.updateProfile(loginMember, request)
    }
}
