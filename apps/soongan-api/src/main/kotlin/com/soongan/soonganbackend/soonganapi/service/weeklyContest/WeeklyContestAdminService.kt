package com.soongan.soonganbackend.soonganapi.service.weeklyContest

import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.request.CreateWeeklyContestAdminRequestDto
import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.response.WeeklyContestAdminResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestAdapter
import org.springframework.stereotype.Service

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
}