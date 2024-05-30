package com.soongan.soonganbackend.persistence.member

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<MemberEntity, Long> {
    fun findByEmail(email: String): MemberEntity?
}
