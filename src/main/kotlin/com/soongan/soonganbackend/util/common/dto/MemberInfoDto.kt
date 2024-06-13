package com.soongan.soonganbackend.util.common.dto

import com.soongan.soonganbackend.persistence.member.MemberEntity

data class MemberInfoDto(
    val nickname: String,
    val profileImageUrl: String
) {
    companion object {
        private const val DEFAULT_NICKNAME = "nickname"
        private const val DEFAULT_PROFILE_IMAGE_URL = "profile_image_url"

        fun from(member: MemberEntity): MemberInfoDto {
            return MemberInfoDto(
                member.nickname ?: DEFAULT_NICKNAME,
                member.profileImageUrl ?: DEFAULT_PROFILE_IMAGE_URL
            )
        }
    }
}
