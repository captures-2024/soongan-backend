package com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MemberInfoResponseDto(
    val email: String,
    val nickname: String?,
    val birthYear: Int?,
    val profileImageUrl: String?,
    val selfIntroduction: String?
) {
    companion object {
        fun from(member: MemberEntity): MemberInfoResponseDto {
            return MemberInfoResponseDto(
                email = member.email,
                nickname = member.nickname,
                birthYear = member.birthYear,
                profileImageUrl = member.profileImageUrl,
                selfIntroduction = member.selfIntroduction
            )
        }
    }
}
