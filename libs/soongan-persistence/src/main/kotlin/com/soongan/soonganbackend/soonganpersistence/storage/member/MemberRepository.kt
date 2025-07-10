package com.soongan.soonganbackend.soonganpersistence.storage.member

import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<MemberEntity, Long> {
    fun findByEmail(email: String): MemberEntity?
    fun findByProviderAndProviderId(provider: ProviderEnum, providerId: String): MemberEntity?
    fun findByNickname(nickname: String): MemberEntity?

    fun findAllByEmail(email: String): List<MemberEntity>
    fun findAllByNickname(nickname: String): List<MemberEntity>
}
