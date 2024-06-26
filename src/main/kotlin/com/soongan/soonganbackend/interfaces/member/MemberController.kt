package com.soongan.soonganbackend.interfaces.member

import com.soongan.soonganbackend.interfaces.member.dto.LoginDto
import com.soongan.soonganbackend.interfaces.member.dto.LoginResultDto
import com.soongan.soonganbackend.service.member.MemberService
import com.soongan.soonganbackend.util.common.constant.Uri
import io.swagger.annotations.ApiOperation
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

    @ApiOperation("로그인")
    @PostMapping(Uri.LOGIN)
    fun login(@RequestBody @Valid loginDto: LoginDto): LoginResultDto {
        return memberService.login(loginDto)
    }
}
