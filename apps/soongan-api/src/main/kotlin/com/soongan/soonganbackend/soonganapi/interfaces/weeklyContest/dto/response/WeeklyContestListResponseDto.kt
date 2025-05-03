package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class WeeklyContestListResponseDto(
    @Schema(description = "주간 콘테스트 목록", required = true)
    val contests: List<WeeklyContestDto>,
) {

    data class WeeklyContestDto(
        @Schema(required = true, description = "주간 콘테스트 ID")
        val id: Long,

        @Schema(required = true, description = "주간 콘테스트 라운드")
        val round: Int,

        @Schema(required = true, description = "주간 콘테스트 주제")
        val subject: String,

        @Schema(required = true, description = "주간 콘테스트 시작 일자")
        val startAt: LocalDateTime,

        @Schema(required = true, description = "주간 콘테스트 종료 일자")
        val endAt: LocalDateTime,

        @Schema(required = true, description = "주간 콘테스트 투표 시작 일자")
        val voteStartAt: LocalDateTime,

        @Schema(required = true, description = "주간 콘테스트 투표 종료 일자")
        val voteEndAt: LocalDateTime,

        @Schema(required = true, description = "주간 콘테스트 공지 일자")
        val announcedAt: LocalDateTime,
    )

    companion object {
        fun from(weeklyContests: List<WeeklyContestEntity>): WeeklyContestListResponseDto {
            return WeeklyContestListResponseDto(
                contests = weeklyContests.map {
                    WeeklyContestDto(
                        id = it.id!!,
                        round = it.round,
                        subject = it.subject,
                        startAt = it.startAt,
                        endAt = it.endAt,
                        voteStartAt = it.voteStartAt,
                        voteEndAt = it.voteEndAt,
                        announcedAt = it.announcedAt,
                    )
                }
            )
        }
    }
}