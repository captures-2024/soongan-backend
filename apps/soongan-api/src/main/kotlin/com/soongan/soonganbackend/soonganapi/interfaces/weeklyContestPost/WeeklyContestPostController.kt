package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost

import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.response.MyWeeklyContestPostResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.request.WeeklyContestPostRegisterRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.response.WeeklyContestPostRegisterResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.response.WeeklyContestPostResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganapi.service.weeklyContestPost.WeeklyContestPostOrderCriteriaEnum
import com.soongan.soonganbackend.soonganapi.service.weeklyContestPost.WeeklyContestService
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.WEEKLY + Uri.CONTESTS + Uri.POSTS)
@Tag(name = "Weekly Contest Apis", description = "주간 콘테스트 관련 API")
class WeeklyContestPostController (
    private val weeklyContestService: WeeklyContestService
){


    @GetMapping
    @Operation(summary = "주간 콘테스트 게시글 조회 Api", description = "주간 콘테스트 게시글을 조회합니다. 라운드와 정렬 기준을 이용하여 조회할 수 있습니다.")
    fun getWeeklyContestPost(
        @RequestParam round: Int,
        @RequestParam orderCriteria: WeeklyContestPostOrderCriteriaEnum,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "50") pageSize: Int
    ): WeeklyContestPostResponseDto {
        return weeklyContestService.getWeeklyContestPostList(round, orderCriteria, page, pageSize)
    }

    @GetMapping(Uri.MY_HISTORY)
    @Operation(summary = "내 주간 콘테스트 게시글 조회 Api", description = "내가 작성한 주간 콘테스트 게시글을 조회합니다.")
    fun getMyWeeklyContestPost(
        @LoginMember loginMember: MemberEntity,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "50") pageSize: Int
    ): MyWeeklyContestPostResponseDto {
        return weeklyContestService.getMyWeeklyContestPostList(loginMember, page, pageSize)
    }

    @PostMapping
    @Operation(summary = "주간 콘테스트 게시글 등록 Api", description = "주간 콘테스트 게시글을 등록합니다.")
    fun registerWeeklyContestPost(
        @LoginMember loginMember: MemberEntity,
        @ModelAttribute @Valid weeklyContestPostRegisterRequest: WeeklyContestPostRegisterRequestDto
    ): WeeklyContestPostRegisterResponseDto {
        return weeklyContestService.registerWeeklyContestPost(loginMember, weeklyContestPostRegisterRequest)
    }

    @DeleteMapping(Uri.POSTS)
    @Operation(summary = "내 주간 콘테스트 게시글 삭제 Api", description = "내가 작성한 주간 콘테스트 게시글을 삭제합니다.")
    fun deleteMyWeeklyContestPost(
        @LoginMember loginMember: MemberEntity,
        @RequestBody postId: Long
    ) {
        weeklyContestService.deleteMyWeeklyContestPost(loginMember, postId)
    }
}
