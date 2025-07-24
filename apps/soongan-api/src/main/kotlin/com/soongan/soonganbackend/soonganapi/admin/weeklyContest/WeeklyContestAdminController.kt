package com.soongan.soonganbackend.soonganapi.admin.weeklyContest

import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.request.CreateWeeklyContestAdminRequestDto
import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.request.UpdateWeeklyContestAdminRequestDto
import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.response.WeeklyContestAdminResponseDto
import com.soongan.soonganbackend.soonganapi.service.weeklyContest.WeeklyContestAdminService
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.ADMIN + Uri.WEEKLY + Uri.CONTESTS)
@Tag(name = "Weekly Contest Admin Apis", description = "주간 콘테스트 관련 Admin API")
class WeeklyContestAdminController(
    private val weeklyContestAdminService: WeeklyContestAdminService
) {

    @Operation(
        summary = "주간 콘테스트 목록 조회",
        description = "주간 콘테스트 목록을 조회합니다."
    )
    @GetMapping
    fun getWeeklyContests(): List<WeeklyContestAdminResponseDto> {
        return weeklyContestAdminService.getWeeklyContests()
    }

    @Operation(
        summary = "새로운 주간 콘테스트 생성",
        description = "새로운 주간 콘테스트를 생성합니다."
    )
    @PostMapping
    fun createWeeklyContest(@RequestBody requestDto: CreateWeeklyContestAdminRequestDto): WeeklyContestAdminResponseDto {
        return weeklyContestAdminService.createWeeklyContest(requestDto)
    }

    @Operation(
        summary = "주간 콘테스트 수정",
        description = "주간 콘테스트를 수정합니다."
    )
    @PatchMapping("/{contestId}")
    fun updateWeeklyContest(
        @PathVariable contestId: Long,
        @RequestBody requestDto: UpdateWeeklyContestAdminRequestDto,
    ): WeeklyContestAdminResponseDto {
        return weeklyContestAdminService.updateWeeklyContest(contestId, requestDto)
    }

    @Operation(
        summary = "주간 콘테스트 삭제",
        description = "주간 콘테스트를 삭제합니다."
    )
    @DeleteMapping("/{contestId}")
    fun deleteWeeklyContest(@PathVariable contestId: Long) {
        weeklyContestAdminService.deleteWeeklyContest(contestId)
    }
}