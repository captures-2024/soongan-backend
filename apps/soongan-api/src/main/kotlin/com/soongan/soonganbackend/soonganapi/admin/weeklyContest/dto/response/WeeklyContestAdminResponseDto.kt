package com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "주간 콘테스트 응답 Admin DTO")
data class WeeklyContestAdminResponseDto(
    @Schema(description = "주간 콘테스트 ID")
    val id: Long,

    @Schema(description = "라운드")
    val round: Int,

    @Schema(description = "주제")
    val subject: String,

    @Schema(description = "1인당 최대 게시물 허용 수")
    val maxPostAllowed: Int,

    @Schema(description = "콘테스트 시작 시간")
    val startAt: LocalDateTime,

    @Schema(description = "콘테스트 종료 시간")
    val endAt: LocalDateTime,

    @Schema(description = "콘테스트 발표 시간")
    val announcedAt: LocalDateTime,

    @Schema(description = "생성 시간")
    val createdAt: LocalDateTime,

    @Schema(description = "수정 시간")
    val updatedAt: LocalDateTime
) {

    companion object {
        fun from(weeklyContestEntity: WeeklyContestEntity): WeeklyContestAdminResponseDto {
            return WeeklyContestAdminResponseDto(
                id = weeklyContestEntity.id,
                round = weeklyContestEntity.round,
                subject = weeklyContestEntity.subject,
                maxPostAllowed = weeklyContestEntity.maxPostAllowed,
                startAt = weeklyContestEntity.startAt,
                endAt = weeklyContestEntity.endAt,
                announcedAt = weeklyContestEntity.announcedAt,
                createdAt = weeklyContestEntity.createdAt,
                updatedAt = weeklyContestEntity.updatedAt
            )
        }
    }
}
