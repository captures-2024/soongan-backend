package com.soongan.soonganbackend.soonganpersistence.storage.member

import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Component
class MemberAdapter (
    private val memberRepository: MemberRepository
){
    @Transactional
    fun save(member: MemberEntity): MemberEntity {
        return memberRepository.save(member)
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): Optional<MemberEntity> {
        return memberRepository.findById(id)
    }

    @Transactional(readOnly = true)
    fun getByEmail(email: String): MemberEntity? {
        return memberRepository.findByEmail(email)
    }

    @Transactional(readOnly = true)
    fun getByProviderAndProviderId(provider: ProviderEnum, providerId: String): MemberEntity? {
        return memberRepository.findByProviderAndProviderId(provider, providerId)
    }

    @Transactional(readOnly = true)
    fun getByNickname(nickname: String): MemberEntity? {
        return memberRepository.findByNickname(nickname)
    }

    // admin용
    @Transactional(readOnly = true)
    fun getAllByEmail(email: String): List<MemberEntity> {
        return memberRepository.findAllByEmail(email)
    }

    // admin용
    @Transactional(readOnly = true)
    fun getAllByNickname(nickname: String): List<MemberEntity> {
        return memberRepository.findAllByNickname(nickname)
    }

    @Transactional
    fun deleteOne(id: Long): Unit {
        memberRepository.deleteById(id)
    }
}
