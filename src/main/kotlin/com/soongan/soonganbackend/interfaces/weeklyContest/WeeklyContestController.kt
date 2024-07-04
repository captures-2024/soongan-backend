package com.soongan.soonganbackend.interfaces.weeklyContest

import com.soongan.soonganbackend.interfaces.weeklyContest.dto.WeeklyContestPostRegisterRequestDto
import com.soongan.soonganbackend.interfaces.weeklyContest.dto.WeeklyContestPostRegisterResponseDto
import com.soongan.soonganbackend.interfaces.weeklyContest.dto.WeeklyContestPostResponseDto
import com.soongan.soonganbackend.service.weeklyContest.WeeklyContestPostOrderCriteriaEnum
import com.soongan.soonganbackend.service.weeklyContest.WeeklyContestService
import com.soongan.soonganbackend.util.common.constant.Uri
import com.soongan.soonganbackend.util.common.dto.MemberDetail
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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

    @PostMapping(Uri.POSTS)
    fun registerWeeklyContestPost(
        @AuthenticationPrincipal loginMember: MemberDetail,
        @RequestBody @Valid weeklyContestPostRegisterRequest: WeeklyContestPostRegisterRequestDto
    ): WeeklyContestPostRegisterResponseDto {
        return weeklyContestService.registerWeeklyContestPost(loginMember, weeklyContestPostRegisterRequest)
    }
}
