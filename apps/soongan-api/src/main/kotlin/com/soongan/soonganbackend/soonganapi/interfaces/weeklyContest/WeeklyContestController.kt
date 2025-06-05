package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest

import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.request.WeeklyContestPostUpdateRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.request.WeeklyContestPostRegisterRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.response.*
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.WeeklyContestPostOrderCriteriaEnum
import com.soongan.soonganbackend.soonganapi.service.weeklyContestPost.WeeklyContestService
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
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

    @Operation(
        summary = "역대 주간 콘테스트 조회",
        description = "역대 주간 콘테스트 리스트를 조회합니다."
    )
    @GetMapping(Uri.HISTORIES)
    fun getWeeklyContestList(): WeeklyContestListResponseDto {
        return weeklyContestService.getWeeklyContestList()
    }

    @Operation(
        summary = "특정 역대 주간 콘테스트 조회",
        description = "특정 역대 주간 콘테스트를 상세 조회합니다. 해당 콘테스트의 1차 투표 상위 7개 게시글을 함께 조회합니다."
    )
    @GetMapping(Uri.HISTORIES + "/{contestId}")
    fun getWeeklyContestDetail(@PathVariable contestId: Long): WeeklyContestDetailResponseDto {
        return weeklyContestService.getWeeklyContestDetail(contestId)
    }

    @Operation(
        summary = "주간 콘테스트 게시글 단일 조회 Api",
        description = "주간 콘테스트 게시글을 단일 조회합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    @GetMapping(Uri.POSTS + "/{postId}")
    fun getWeeklyContestPost(
        @LoginMember(throwIfUnauthorized = false) loginMember: MemberEntity?,
        @PathVariable postId: Long
    ): WeeklyContestPostResponseDto {
        return weeklyContestService.getWeeklyContestPost(postId, loginMember)
    }

    @Operation(
        summary = "주간 콘테스트 게시글 조회 Api",
        description = "주간 콘테스트 게시글을 조회합니다. 라운드와 정렬 기준을 이용하여 조회할 수 있습니다."
    )
    @GetMapping(Uri.POSTS)
    fun getWeeklyContestPosts(
        @RequestParam(required = false) round: Int? = null,
        @RequestParam orderCriteria: WeeklyContestPostOrderCriteriaEnum,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "50") pageSize: Int
    ): WeeklyContestPostListResponseDto {
        return weeklyContestService.getWeeklyContestPostList(round, orderCriteria, page, pageSize)
    }

    @Operation(
        summary = "[커서기반] 주간 콘테스트 게시글 조회 Api",
        description = "[커서기반] 주간 콘테스트 게시글을 조회합니다. 라운드와 정렬 기준을 이용하여 조회할 수 있습니다."
    )
    @GetMapping(Uri.POSTS)
    fun getWeeklyContestPosts(
        @RequestParam(required = false) round: Int? = null,
        @RequestParam orderCriteria: WeeklyContestPostOrderCriteriaEnum,
        @RequestParam(required = false) nextCursor: String?,
        @RequestParam(required = false, defaultValue = "50") pageSize: Int
    ): WeeklyContestPostListCursorResponseDto {
        return weeklyContestService.getWeeklyContestPostListWithCursor(round, orderCriteria, nextCursor, pageSize)
    }


    @Operation(
        summary = "내 주간 콘테스트 게시글 조회 Api",
        description = "내가 작성한 주간                                     콘테스트 게시글을 조회합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    @GetMapping(Uri.POSTS + Uri.MY_HISTORY)
    fun getMyWeeklyContestPost(
        @LoginMember loginMember: MemberEntity,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "50") pageSize: Int
    ): MyWeeklyContestPostResponseDto {
        return weeklyContestService.getMyWeeklyContestPostList(loginMember, page, pageSize)
    }

    @Operation(
        summary = "주간 콘테스트 게시글 등록 Api",
        description = "주간 콘테스트 게시글을 등록합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    @PostMapping(Uri.POSTS)
    fun registerWeeklyContestPost(
        @LoginMember loginMember: MemberEntity,
        @ModelAttribute @Valid weeklyContestPostRegisterRequest: WeeklyContestPostRegisterRequestDto
    ): WeeklyContestPostRegisterResponseDto {
        return weeklyContestService.registerWeeklyContestPost(loginMember, weeklyContestPostRegisterRequest)
    }

    @Operation(
        summary = "주간 콘테스트 게시글 수정 Api",
        description = "주간 콘테스트 게시글을 수정합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    @PatchMapping(Uri.POSTS + "/{postId}")
    fun updateWeeklyContestPost(
        @LoginMember loginMember: MemberEntity,
        @PathVariable postId: Long,
        @RequestBody @Valid weeklyContestPostRegisterRequest: WeeklyContestPostUpdateRequestDto
    ): WeeklyContestPostUpdateResponseDto {
        return weeklyContestService.updateWeeklyContestPost(loginMember, postId, weeklyContestPostRegisterRequest)
    }

    @Operation(
        summary = "내 주간 콘테스트 게시글 삭제 Api",
        description = "내가 작성한 주간 콘테스트 게시글을 삭제합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    @DeleteMapping(Uri.POSTS + "/{postId}")
    fun deleteMyWeeklyContestPost(
        @LoginMember loginMember: MemberEntity,
        @PathVariable postId: Long
    ) {
        weeklyContestService.deleteMyWeeklyContestPost(loginMember, postId)
    }
}