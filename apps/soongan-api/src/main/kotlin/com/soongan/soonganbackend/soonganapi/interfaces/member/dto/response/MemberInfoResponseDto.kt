package com.soongan.soonganbackend.soonganapi.interfaces.member.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.soongan.soonganbackend.soonganapi.interfaces.report.dto.response.ReportHistoryResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.report.ReportEntity
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "회원 정보 응답 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class MemberInfoResponseDto(
    @Schema(description = "이메일", required = true)
    val email: String,

    @Schema(description = "닉네임")
    val nickname: String?,

    @Schema(description = "출생년도")
    val birthYear: Int?,

    @Schema(description = "프로필 이미지 URL")
    val profileImageUrl: String?,

    @Schema(description = "자기소개")
    val selfIntroduction: String?,

    @Schema(description = "유저가 신고한 내역")
    val reportHistories: List<ReportHistoryResponseDto>
) {
    companion object {
        fun from(member: MemberEntity, reportHistories: List<ReportEntity>): MemberInfoResponseDto {
            val reportHistoryResponseDtos = reportHistories.map { reportHistory ->
                ReportHistoryResponseDto.from(reportHistory)
            }

            return MemberInfoResponseDto(
                email = member.email,
                nickname = member.nickname,
                birthYear = member.birthYear,
                profileImageUrl = member.profileImageUrl,
                selfIntroduction = member.selfIntroduction,
                reportHistories = reportHistoryResponseDtos
            )
        }
    }
}