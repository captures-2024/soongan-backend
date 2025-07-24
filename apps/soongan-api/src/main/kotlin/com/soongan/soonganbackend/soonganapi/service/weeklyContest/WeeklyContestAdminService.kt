package com.soongan.soonganbackend.soonganapi.service.weeklyContest

import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.request.CreateWeeklyContestAdminRequestDto
import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.request.UpdateWeeklyContestAdminRequestDto
import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.response.WeeklyContestAdminResponseDto
import com.soongan.soonganbackend.soonganapi.service.weeklyContest.validator.WeeklyContestValidator
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class WeeklyContestAdminService(
    private val weeklyContestAdapter: WeeklyContestAdapter,
    private val weeklyContestValidator: WeeklyContestValidator
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
        val existWeeklyContestEntity = weeklyContestValidator.canUpdateWeeklyContest(contestId)
        weeklyContestValidator.validateUpdateWeeklyContestDto(requestDto)

        val updatedWeeklyContestEntity = weeklyContestAdapter.save(
            requestDto.toWeeklyContestEntity(existWeeklyContestEntity = existWeeklyContestEntity)
        )
        return WeeklyContestAdminResponseDto.Companion.from(updatedWeeklyContestEntity)
    }

    fun deleteWeeklyContest(contestId: Long) {
        weeklyContestValidator.canUpdateWeeklyContest(contestId)
        weeklyContestAdapter.deleteWeeklyContestById(contestId)
    }
}