package com.soongan.soonganbackend.persistence.member

import com.soongan.soonganbackend.dto.LoginDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController(
    private val memberService: MemberService
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDto: LoginDto): String {
        return memberService.login(loginDto)
    }
}