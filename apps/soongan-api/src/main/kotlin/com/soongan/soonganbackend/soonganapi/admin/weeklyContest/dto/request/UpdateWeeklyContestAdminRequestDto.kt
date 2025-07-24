package com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.request

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "주간 콘테스트 수정 요청 Admin DTO")
data class UpdateWeeklyContestAdminRequestDto(
    @Schema(description = "주제")
    val subject: String? = null,

    @Schema(description = "1인당 등록 허용 게시글 수", defaultValue = "3")
    val maxPostAllowed: Int? = null,

    @Schema(description = "콘테스트 시작 시간")
    val startAt: LocalDateTime? = null,

    @Schema(description = "콘테스트 종료 시간")
    val endAt: LocalDateTime? = null,

    @Schema(description = "콘테스트 노출 시작 시간")
    val announcedAt: LocalDateTime? = null,
) {

    fun toWeeklyContestEntity(existWeeklyContestEntity: WeeklyContestEntity): WeeklyContestEntity {
        return existWeeklyContestEntity.copy(
            subject = subject ?: existWeeklyContestEntity.subject,
            maxPostAllowed = maxPostAllowed ?: existWeeklyContestEntity.maxPostAllowed,
            startAt = startAt ?: existWeeklyContestEntity.startAt,
            endAt = endAt ?: existWeeklyContestEntity.endAt,
            announcedAt = announcedAt ?: existWeeklyContestEntity.announcedAt
        )
    }
}