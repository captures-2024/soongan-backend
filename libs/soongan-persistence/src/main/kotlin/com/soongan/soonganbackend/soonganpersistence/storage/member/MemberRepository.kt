package com.soongan.soonganbackend.soonganpersistence.storage.member

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<MemberEntity, Long> {
    fun findByEmail(email: String): MemberEntity?
    fun findByNickname(nickname: String): MemberEntity?

    fun deleteByEmail(email: String)
}
