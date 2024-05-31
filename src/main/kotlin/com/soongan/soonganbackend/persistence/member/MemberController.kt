package com.soongan.soonganbackend.persistence.member

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/members")
class MemberController {

    @PostMapping("/login")
    fun login(): String {
        return "login"
    }
}