package com.soongan.soonganbackend.repository

import com.soongan.soonganbackend.model.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository: JpaRepository<Member, Long>