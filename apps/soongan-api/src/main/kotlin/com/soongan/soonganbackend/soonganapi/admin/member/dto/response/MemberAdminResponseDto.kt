package com.soongan.soonganbackend.soonganapi.admin.member.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "회원 정보 어드민 응답 DTO")
data class MemberAdminResponseDto(
    @Schema(description = "회원 ID")
    val id: Long,

    @Schema(description = "회원 이메일")
    val email: String,

    @Schema(description = "이메일 제공자 (ex. google, kakao, apple)")
    val provider: ProviderEnum,

    @Schema(description = "회원 이름")
    val nickname: String?,

    @Schema(description = "회원 자기 소개")
    val selfIntroduction: String?,

    @Schema(description = "회원 프로필 이미지 URL")
    val profileImageUrl: String?,

    @Schema(description = "탈퇴 날짜")
    val withdrawalAt: LocalDateTime?,

    @Schema(description = "정지 끝나는 날짜 (해당 날짜 이전이라면 정지 상태 아님)")
    val banUntil: LocalDateTime?,

    @Schema(description = "회원 가입일")
    val createdAt: LocalDateTime,

    @Schema(description = "회원 정보 수정일")
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(memberEntity: MemberEntity): MemberAdminResponseDto {
            return MemberAdminResponseDto(
                id = memberEntity.id!!,
                email = memberEntity.email,
                provider = memberEntity.provider,
                nickname = memberEntity.nickname,
                selfIntroduction = memberEntity.selfIntroduction,
                profileImageUrl = memberEntity.profileImageUrl,
                withdrawalAt = memberEntity.withdrawalAt,
                banUntil = memberEntity.banUntil,
                createdAt = memberEntity.createdAt,
                updatedAt = memberEntity.updatedAt
            )
        }
    }
}
