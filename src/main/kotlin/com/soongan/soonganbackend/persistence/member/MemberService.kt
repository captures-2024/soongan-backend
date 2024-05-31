package com.soongan.soonganbackend.persistence.member

import com.soongan.soonganbackend.dto.LoginDto
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {

    fun login(loginDto: LoginDto): String {
        val provider = loginDto.provider
        val accessToken = loginDto.accessToken
        return "provider: $provider, accessToken: $accessToken"
    }
}