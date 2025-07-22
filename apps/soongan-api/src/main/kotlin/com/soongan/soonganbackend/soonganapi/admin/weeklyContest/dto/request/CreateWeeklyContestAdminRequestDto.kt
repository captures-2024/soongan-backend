package com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.request

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Schema(description = "주간 콘테스트 생성 요청 Admin DTO. round는 자동 생성됩니다.")
data class CreateWeeklyContestAdminRequestDto(
    @Schema(description = "주제")
    @field:NotNull
    val subject: String,

    @Schema(description = "1인당 등록 허용 게시글 수", defaultValue = "3")
    @field:NotNull
    val maxPostAllowed: Int,

    @Schema(description = "콘테스트 시작 시간")
    @field:NotNull
    val startAt: LocalDateTime,

    @Schema(description = "콘테스트 종료 시간")
    @field:NotNull
    val endAt: LocalDateTime,

    @Schema(description = "콘테스트 노출 시작 시간")
    @field:NotNull
    val announcedAt: LocalDateTime,
) {

    fun toWeeklyContestEntity(round: Int): WeeklyContestEntity {
        return WeeklyContestEntity(
            subject = subject,
            round = round,
            maxPostAllowed = maxPostAllowed,
            startAt = startAt,
            endAt = endAt,
            announcedAt = announcedAt
        )
    }
}
