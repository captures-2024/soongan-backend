package com.soongan.soonganbackend.soonganapi.interfaces.member

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganapi.service.member.MemberService
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response.MemberInfoResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response.UpdateBirthDateResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response.UpdateNicknameResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response.UpdateProfileImageResponseDto
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

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

    @Operation(summary = "닉네임 변경 Api", description = "닉네임을 변경합니다.")
    @PatchMapping(Uri.NICKNAME)
    fun updateNickname(@LoginMember loginMember: MemberEntity, @RequestParam newNickname: String): UpdateNicknameResponseDto {
        return memberService.updateNickname(loginMember, newNickname)
    }

    @Operation(summary = "프로필 사진 변경 Api", description = "프로필 사진을 변경합니다.")
    @PatchMapping(Uri.PROFILE_IMAGE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProfileImage(@LoginMember loginMember: MemberEntity, @RequestPart("image") profileImage: MultipartFile): UpdateProfileImageResponseDto {
        return memberService.updateProfileImage(loginMember, profileImage)
    }

    @Operation(summary = "생년월일 변경 Api", description = "생년월일을 변경합니다.")
    @PatchMapping(Uri.BIRTH_DATE)
    fun updateBirthDate(@LoginMember loginMember: MemberEntity, @RequestParam birthDate: LocalDate): UpdateBirthDateResponseDto {
        println(birthDate)
        return memberService.updateBirthDate(loginMember, birthDate)
    }
}
