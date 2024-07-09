package com.soongan.soonganbackend.interfaces.member

import com.soongan.soonganbackend.enums.UserAgent
import com.soongan.soonganbackend.interfaces.member.dto.LoginRequestDto
import com.soongan.soonganbackend.interfaces.member.dto.LoginResponseDto
import com.soongan.soonganbackend.service.member.MemberService
import com.soongan.soonganbackend.util.common.constant.Uri
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.MEMBERS)
@Tag(name = "Member Apis", description = "회원 관리 API")
class MemberController(
    private val memberService: MemberService
) {

    @Operation(summary = "로그인 Api", description = "idToken을 이용하여 로그인을 수행하고, JWT 토큰을 발급합니다.")
    @PostMapping(Uri.LOGIN)
    fun login(@RequestHeader(value = "User-Agent") userAgent: UserAgent, @RequestBody @Valid loginDto: LoginRequestDto): LoginResponseDto {
        return memberService.login(userAgent, loginDto)
    }
}
