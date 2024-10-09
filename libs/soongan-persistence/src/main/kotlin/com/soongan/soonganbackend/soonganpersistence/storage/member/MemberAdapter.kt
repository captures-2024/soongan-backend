package com.soongan.soonganbackend.soonganpersistence.storage.member

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberAdapter (
    private val memberRepository: MemberRepository
){
    @Transactional
    fun save(member: MemberEntity): MemberEntity {
        return memberRepository.save(member)
    }

    @Transactional(readOnly = true)
    fun getByEmail(email: String): MemberEntity? {
        return memberRepository.findByEmail(email)
    }

    @Transactional(readOnly = true)
    fun getByNickname(nickname: String): MemberEntity? {
        return memberRepository.findByNickname(nickname)
    }
}
