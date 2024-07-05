package com.soongan.soonganbackend.interfaces.member

import com.soongan.soonganbackend.interfaces.member.dto.LoginRequestDto
import com.soongan.soonganbackend.interfaces.member.dto.LoginResponseDto
import com.soongan.soonganbackend.service.member.MemberService
import com.soongan.soonganbackend.util.common.constant.Uri
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.MEMBERS)
class MemberController(
    private val memberService: MemberService
) {

    @PostMapping(Uri.LOGIN)
    fun login(@RequestBody @Valid loginDto: LoginRequestDto): LoginResponseDto {
        return memberService.login(loginDto)
    }
}
