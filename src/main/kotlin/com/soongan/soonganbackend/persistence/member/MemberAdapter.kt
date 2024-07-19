package com.soongan.soonganbackend.persistence.member

import org.springframework.stereotype.Component

@Component
class MemberAdapter (
    private val memberRepository: MemberRepository
){
    fun save(member: MemberEntity): MemberEntity {
        return memberRepository.save(member)
    }

    fun getByEmail(email: String): MemberEntity? {
        return memberRepository.findByEmail(email)
    }

    fun deleteByEmail(email: String) {
        memberRepository.deleteByEmail(email)
    }
}
