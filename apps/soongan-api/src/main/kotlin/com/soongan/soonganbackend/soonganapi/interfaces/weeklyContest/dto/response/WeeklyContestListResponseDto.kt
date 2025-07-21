package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "주간 콘테스트 목록 조회 응답 DTO")
data class WeeklyContestListResponseDto(
    @Schema(description = "주간 콘테스트 목록")
    val contests: List<WeeklyContestOverviewDto>,
) {

    data class WeeklyContestOverviewDto(
        @Schema(description = "주간 콘테스트 ID", required = true)
        val id: Long,

        @Schema(description = "주간 콘테스트 라운드", required = true)
        val round: Int,

        @Schema(description = "주간 콘테스트 주제", required = true)
        val subject: String,
    )

    companion object {
        fun from(contests: List<WeeklyContestEntity>): WeeklyContestListResponseDto {
            return WeeklyContestListResponseDto(
                contests = contests.map {
                    WeeklyContestOverviewDto(
                        id = it.id,
                        round = it.round,
                        subject = it.subject
                    )
                }
            )
        }
    }
}
