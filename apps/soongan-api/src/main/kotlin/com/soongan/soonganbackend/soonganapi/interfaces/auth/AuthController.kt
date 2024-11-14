package com.soongan.soonganbackend.soonganapi.interfaces.auth

import com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.request.LoginRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.response.LoginResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.request.RefreshRequestDto
import com.soongan.soonganbackend.soonganapi.service.auth.AuthService
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Uri.AUTH)
@Tag(name = "Auth Apis", description = "인증 관련 API")
class AuthController(
    private val authService: AuthService
) {

    @Operation(summary = "로그인 Api", description = "idToken을 이용하여 로그인을 수행하고, JWT를 발급합니다.")
    @PostMapping(Uri.LOGIN)
    fun login(@RequestHeader(value = "User-Agent") userAgent: UserAgentEnum, @RequestBody @Valid loginDto: LoginRequestDto): LoginResponseDto {
        return authService.login(userAgent, loginDto)
    }

    @Operation(summary = "로그아웃 Api", description = "로그인시 발급한 JWT를 말소합니다.")
    @PostMapping(Uri.LOGOUT)
    fun logout(@LoginMember loginMember: MemberEntity) {
        authService.logout(loginMember)
    }

    @Operation(summary = "회원 탈퇴 Api", description = "회원을 탈퇴합니다.")
    @PostMapping(Uri.WITHDRAW)
    fun withdraw(@LoginMember loginMember: MemberEntity) {
        authService.withdraw(loginMember)
    }

    @Operation(summary = "JWT 갱신 Api", description = "Refresh Token을 이용하여 JWT를 갱신합니다.")
    @PatchMapping(Uri.REFRESH)
    fun refresh(@RequestBody @Valid refreshRequestDto: RefreshRequestDto): LoginResponseDto {
        return authService.refresh(refreshRequestDto)
    }
}