package com.soongan.soonganbackend.soonganapi.interfaces.home

import com.soongan.soonganbackend.soonganapi.interfaces.home.dto.response.HomeResponseDto
import com.soongan.soonganbackend.soonganapi.service.home.HomeService
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(Uri.HOME)
@RestController
@Tag(name = "Home Apis", description = "홈 관련 API")
class HomeController (
    private val homeService: HomeService
){

    @Operation(summary = "Home 화면 API", description = "현재 진행 중인 Weekly Contest 정보와 출품한 게시글을 조회합니다.")
    @GetMapping
    fun getHome(@LoginMember loginMember: MemberEntity): HomeResponseDto {
        return homeService.getHome(loginMember)
    }
}
