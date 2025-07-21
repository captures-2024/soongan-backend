package com.soongan.soonganbackend.soonganapi.admin.weeklyContest

import com.soongan.soonganbackend.soonganapi.admin.weeklyContest.dto.response.WeeklyContestAdminResponseDto
import com.soongan.soonganbackend.soonganapi.service.weeklyContest.WeeklyContestAdminService
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
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
}