package com.soongan.soonganbackend.soonganapi.service.weeklyContest.validator

import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.request.UpdateWeeklyContestAdminRequestDto
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestStatusEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class WeeklyContestValidator(
    private val weeklyContestAdapter: WeeklyContestAdapter
) {

    /**
     * 주어진 라운드에 해당하는 주간 콘테스트를 반환한다.
     * 라운드가 주어지지 않으면, 현재 진행 중인 주간 콘테스트를 반환한다.
     * 현재 진행 중인 주간 콘테스트가 없으면, 가장 최신 종료된 주간 콘테스트를 반환한다.
     */
    fun getWeeklyContestIfValidRound(round: Int? = null): WeeklyContestEntity {
        val now = LocalDateTime.now()

        return round?.let {
            weeklyContestAdapter.getWeeklyContestByRound(round)
                ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST)
        }
            ?: weeklyContestAdapter.getInProgressWeeklyContest(now)
        ?: weeklyContestAdapter.getLatestEndedWeeklyContest(now)
        ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_WEEKLY_CONTEST)
    }

    fun canUpdateWeeklyContest(
        contestId: Long,
    ): WeeklyContestEntity {
        val existWeeklyContestEntity = weeklyContestAdapter.getWeeklyContestById(contestId)
            ?: throw SoonganException(statusCode = StatusCode.NOT_FOUND, "해당 콘테스트가 존재하지 않습니다. contestId: $contestId")

        val now = LocalDateTime.now()

        if (existWeeklyContestEntity.announcedAt < now) {
            throw SoonganException(statusCode = StatusCode.BAD_REQUEST, "이미 노출된 콘테스트는 수정할 수 없습니다.")
        }

        return existWeeklyContestEntity
    }

    fun validateUpdateWeeklyContestDto(
        requestDto: UpdateWeeklyContestAdminRequestDto
    ) {
        val now = LocalDateTime.now()

        if ((requestDto.announcedAt != null && requestDto.startAt != null && requestDto.announcedAt > requestDto.startAt) ||
            (requestDto.startAt != null && requestDto.endAt != null && requestDto.startAt > requestDto.endAt) ||
            (requestDto.announcedAt != null && requestDto.endAt != null && requestDto.announcedAt > requestDto.endAt)) {
            throw SoonganException(statusCode = StatusCode.BAD_REQUEST, "콘테스트 시간 설정이 올바르지 않습니다. announcedAt, startAt, endAt의 순서로 시간이 설정되어야 합니다.")
        }

        if (
            (requestDto.announcedAt != null && requestDto.announcedAt < now) ||
            (requestDto.startAt != null && requestDto.startAt < now) ||
            (requestDto.endAt != null && requestDto.endAt < now)
        ) {
            throw SoonganException(statusCode = StatusCode.BAD_REQUEST, "수정할 콘테스트 시간은 현재 시간 이후여야 합니다.")
        }
    }

    fun determineContestStatus(weeklyContest: WeeklyContestEntity): ContestStatusEnum {
        val now = LocalDateTime.now()

        return when {
            now.isBefore(weeklyContest.startAt) -> ContestStatusEnum.UPCOMING
            now.isAfter(weeklyContest.endAt) -> ContestStatusEnum.CLOSED
            else -> ContestStatusEnum.IN_PROGRESS
        }
    }
}
