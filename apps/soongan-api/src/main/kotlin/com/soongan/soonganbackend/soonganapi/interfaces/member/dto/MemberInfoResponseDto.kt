package com.soongan.soonganbackend.soonganapi.interfaces.member.dto

import com.soongan.soonganbackend.soonganpersistence.storage.persistence.member.MemberEntity
import java.time.LocalDate

data class MemberInfoResponseDto(
    val email: String,
    val nickname: String?,
    val birthDate: LocalDate?,
    val profileImageUrl: String?
) {
    companion object {
        fun from(member: MemberEntity): MemberInfoResponseDto {
            return MemberInfoResponseDto(
                email = member.email,
                nickname = member.nickname,
                birthDate = member.birthDate,
                profileImageUrl = member.profileImageUrl
            )
        }
    }
}
