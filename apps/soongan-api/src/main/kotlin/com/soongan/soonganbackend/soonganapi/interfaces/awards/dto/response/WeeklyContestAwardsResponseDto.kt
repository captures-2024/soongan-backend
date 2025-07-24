package com.soongan.soonganbackend.soonganapi.interfaces.awards.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class WeeklyContestAwardsResponseDto(
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

        @Schema(required = true, description = "주간 콘테스트 공지 일자")
        val announcedAt: LocalDateTime,

        @Schema(required = true, description = "썸네일 이미지 URL (해당 콘테스트 1등 게시글 이미지)")
        val thumbnailImageUrl: String,
    ) {
        companion object {
            fun from(entity: WeeklyContestEntity, thumbnailImageUrl: String): WeeklyContestDto {
                return WeeklyContestDto(
                    id = entity.id,
                    round = entity.round,
                    subject = entity.subject,
                    startAt = entity.startAt,
                    endAt = entity.endAt,
                    announcedAt = entity.announcedAt,
                    thumbnailImageUrl = thumbnailImageUrl,
                )
            }
        }
    }
}