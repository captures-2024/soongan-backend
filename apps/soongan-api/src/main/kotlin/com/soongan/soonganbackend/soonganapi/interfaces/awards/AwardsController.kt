package com.soongan.soonganbackend.soonganapi.interfaces.awards

import com.soongan.soonganbackend.soonganapi.interfaces.awards.dto.response.AwardsDetailResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.awards.dto.response.WeeklyContestAwardsResponseDto
import com.soongan.soonganbackend.soonganapi.service.awards.AwardsService
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.AWARDS)
@Tag(name = "Awards Apis", description = "역대콘 API")
class AwardsController(
    private val awardsService: AwardsService
) {

    @Operation(
        summary = "역대 주간 콘테스트 조회",
        description = "역대 주간 콘테스트 리스트를 조회합니다."
    )
    @GetMapping
    fun getWeeklyContestAwards(): WeeklyContestAwardsResponseDto {
        return awardsService.getWeeklyContestAwards()
    }

    @Operation(
        summary = "특정 역대 주간 콘테스트 조회",
        description = "특정 역대 주간 콘테스트를 상세 조회합니다. 해당 콘테스트의 1차 투표 상위 7개 게시글을 함께 조회합니다."
    )
    @GetMapping("/{contestId}")
    fun getAwardsDetail(@PathVariable contestId: Long): AwardsDetailResponseDto {
        return awardsService.getAwardsDetail(contestId)
    }
}