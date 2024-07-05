package com.soongan.soonganbackend.interfaces.weeklyContest

import com.soongan.soonganbackend.interfaces.weeklyContest.dto.WeeklyContestPostRegisterRequestDto
import com.soongan.soonganbackend.interfaces.weeklyContest.dto.WeeklyContestPostRegisterResponseDto
import com.soongan.soonganbackend.interfaces.weeklyContest.dto.WeeklyContestPostResponseDto
import com.soongan.soonganbackend.service.weeklyContest.WeeklyContestPostOrderCriteriaEnum
import com.soongan.soonganbackend.service.weeklyContest.WeeklyContestService
import com.soongan.soonganbackend.util.common.constant.Uri
import com.soongan.soonganbackend.util.common.dto.MemberDetail
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "Weekly Contest Apis", description = "주간 콘테스트 관련 API")
class WeeklyContestController (
    private val weeklyContestService: WeeklyContestService
){


    @GetMapping(Uri.POSTS)
    @Operation(summary = "주간 콘테스트 게시글 조회 Api", description = "주간 콘테스트 게시글을 조회합니다. 라운드와 정렬 기준을 이용하여 조회할 수 있습니다.")
    fun getWeeklyContestPost(
        @RequestParam round: Int,
        @RequestParam orderCriteria: WeeklyContestPostOrderCriteriaEnum,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "50") pageSize: Int
    ): WeeklyContestPostResponseDto {
        return weeklyContestService.getWeeklyContestPost(round, orderCriteria, page, pageSize)
    }

    @PostMapping(Uri.POSTS)
    @Operation(summary = "주간 콘테스트 게시글 등록 Api", description = "주간 콘테스트 게시글을 등록합니다.")
    fun registerWeeklyContestPost(
        @AuthenticationPrincipal loginMember: MemberDetail,
        @RequestBody @Valid weeklyContestPostRegisterRequest: WeeklyContestPostRegisterRequestDto
    ): WeeklyContestPostRegisterResponseDto {
        return weeklyContestService.registerWeeklyContestPost(loginMember, weeklyContestPostRegisterRequest)
    }
}
