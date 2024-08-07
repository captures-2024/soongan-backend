package com.soongan.soonganbackend.interfaces.member

import com.soongan.soonganbackend.enums.UserAgent
import com.soongan.soonganbackend.interfaces.member.dto.*
import com.soongan.soonganbackend.service.member.MemberService
import com.soongan.soonganbackend.util.common.constant.Uri
import com.soongan.soonganbackend.util.common.dto.MemberDetail
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Uri.MEMBERS)
@Tag(name = "Member Apis", description = "회원 관리 API")
class MemberController(
    private val memberService: MemberService
) {

    @Operation(summary = "로그인 Api", description = "idToken을 이용하여 로그인을 수행하고, JWT를 발급합니다.")
    @PostMapping(Uri.LOGIN)
    fun login(@RequestHeader(value = "User-Agent") userAgent: UserAgent, @RequestBody @Valid loginDto: LoginRequestDto): LoginResponseDto {
        return memberService.login(userAgent, loginDto)
    }

    @Operation(summary = "로그아웃 Api", description = "로그인시 발급한 JWT를 말소합니다.")
    @PostMapping(Uri.LOGOUT)
    fun logout(@AuthenticationPrincipal loginMember: MemberDetail) {
        memberService.logout(loginMember)
    }

    @Operation(summary = "회원 탈퇴 Api", description = "회원을 탈퇴합니다.")
    @PostMapping(Uri.WITHDRAW)
    fun withdraw() {
        memberService.withdraw()
    }

    @Operation(summary = "JWT 갱신 Api", description = "Refresh Token을 이용하여 JWT를 갱신합니다.")
    @PatchMapping(Uri.REFRESH)
    fun refresh(@RequestBody @Valid refreshRequestDto: RefreshRequestDto): LoginResponseDto {
        return memberService.refresh(refreshRequestDto)
    }

    @Operation(summary = "회원 정보 조회 Api", description = "JWT를 읽어 로그인한 회원의 정보를 조회합니다.")
    @GetMapping
    fun getUserinfo(): MemberInfoResponseDto {
        return memberService.getMemberInfo()
    }

    @Operation(summary = "닉네임 중복 확인 Api", description = "닉네임이 중복되는지 확인합니다. true면 사용 가능, false면 중복.")
    @GetMapping(Uri.CHECK_NICKNAME)
    fun checkNickname(@RequestParam nickname: String): Boolean {
        return memberService.checkNickname(nickname)
    }

    @Operation(summary = "닉네임 변경 Api", description = "닉네임을 변경합니다.")
    @PatchMapping(Uri.NICKNAME)
    fun updateNickname(@RequestParam newNickname: String): UpdateNicknameResponseDto {
        return memberService.updateNickname(newNickname)
    }

    @Operation(summary = "프로필 사진 변경 Api", description = "프로필 사진을 변경합니다.")
    @PatchMapping(Uri.PROFILE_IMAGE, consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProfileImage(@RequestPart("image") profileImage: MultipartFile) {
        memberService.updateProfileImage(profileImage)
    }
}
