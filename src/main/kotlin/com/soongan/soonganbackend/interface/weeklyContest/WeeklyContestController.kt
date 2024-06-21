package com.soongan.soonganbackend.`interface`.weeklyContest

import com.soongan.soonganbackend.`interface`.weeklyContest.dto.WeeklyContestPostResponseDto
import com.soongan.soonganbackend.service.weeklyContest.WeeklyContestPostOrderCriteriaEnum
import com.soongan.soonganbackend.service.weeklyContest.WeeklyContestService
import com.soongan.soonganbackend.util.common.constant.Uri
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.WEEKLY + Uri.CONTESTS)
class WeeklyContestController (
    private val weeklyContestService: WeeklyContestService
){

    @GetMapping(Uri.POSTS)
    fun getWeeklyContestPost(
        @RequestParam round: Int,
        @RequestParam orderCriteria: WeeklyContestPostOrderCriteriaEnum,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "50") pageSize: Int
    ): WeeklyContestPostResponseDto {
        return weeklyContestService.getWeeklyContestPost(round, orderCriteria, page, pageSize)
    }
}
