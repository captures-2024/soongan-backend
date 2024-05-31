package com.soongan.soonganbackend.persistence.member

import com.soongan.soonganbackend.dto.LoginDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController {

    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto): String {
        return "login"
    }
}