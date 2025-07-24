package com.soongan.soonganbackend.soonganapi.service.weeklyContest

import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.request.CreateWeeklyContestAdminRequestDto
import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.request.UpdateWeeklyContestAdminRequestDto
import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.response.WeeklyContestAdminResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class WeeklyContestAdminService(
    private val weeklyContestAdapter: WeeklyContestAdapter
) {
    fun getWeeklyContests(): List<WeeklyContestAdminResponseDto> {
        val weeklyContestEntities = weeklyContestAdapter.getAllWeeklyContests()
        return weeklyContestEntities.map { WeeklyContestAdminResponseDto.Companion.from(it) }
    }

    fun createWeeklyContest(requestDto: CreateWeeklyContestAdminRequestDto): WeeklyContestAdminResponseDto {
        val latestRound = weeklyContestAdapter.getLatestRound()
        val createdWeeklyContestEntity = weeklyContestAdapter.save(requestDto.toWeeklyContestEntity(latestRound + 1))
        return WeeklyContestAdminResponseDto.Companion.from(createdWeeklyContestEntity)
    }

    fun updateWeeklyContest(
        contestId: Long,
        requestDto: UpdateWeeklyContestAdminRequestDto
    ): WeeklyContestAdminResponseDto {
        val existWeeklyContestEntity = weeklyContestAdapter.getWeeklyContestById(contestId)
            ?: throw SoonganException(statusCode = StatusCode.NOT_FOUND, "해당 콘테스트가 존재하지 않습니다. contestId: $contestId")

        val now = LocalDateTime.now()

        if (existWeeklyContestEntity.announcedAt < now) {
            throw SoonganException(statusCode = StatusCode.BAD_REQUEST, "이미 노출된 콘테스트는 수정할 수 없습니다.")
        }

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

        val updated = requestDto.toWeeklyContestEntity(existWeeklyContestEntity = existWeeklyContestEntity)
        val updatedWeeklyContestEntity = weeklyContestAdapter.save(updated)
        return WeeklyContestAdminResponseDto.Companion.from(updatedWeeklyContestEntity)
    }

    fun deleteWeeklyContest(contestId: Long) {
        val existWeeklyContestEntity = weeklyContestAdapter.getWeeklyContestById(contestId)
            ?: throw SoonganException(statusCode = StatusCode.NOT_FOUND, "해당 콘테스트가 존재하지 않습니다. contestId: $contestId")

        val now = LocalDateTime.now()

        if (existWeeklyContestEntity.announcedAt < now) {
            throw SoonganException(statusCode = StatusCode.BAD_REQUEST, "이미 노출된 콘테스트는 삭제할 수 없습니다.")
        }

        weeklyContestAdapter.deleteWeeklyContestById(contestId)
    }
}