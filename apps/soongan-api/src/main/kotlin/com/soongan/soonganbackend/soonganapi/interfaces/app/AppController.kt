package com.soongan.soonganbackend.soonganapi.interfaces.app

import com.soongan.soonganbackend.soonganapi.interfaces.app.dto.request.VersionCheckRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.app.dto.response.VersionCheckResponseDto
import com.soongan.soonganbackend.soonganapi.service.app.AppService
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "App Apis", description = "앱 관련 API")
@RestController
@RequestMapping(Uri.APP)
class AppController(
    private val appService: AppService
) {

    @Operation(
        summary = "앱 버전 체크 Api",
        description = "현재 앱 버전을 체크하여 업데이트가 필요한지 확인합니다. " +
                "최신 버전 여부와 강제 업데이트 필요 여부를 반환합니다. " +
                "버전 형식은 Semantic Versioning (major.minor.patch)을 따릅니다. 현재 최신버전: 2.0.1로 세팅되어있습니다."
    )
    @PostMapping(Uri.VERSION + Uri.CHECK)
    fun checkVersion(
        @RequestBody @Valid versionCheckRequestDto: VersionCheckRequestDto
    ): VersionCheckResponseDto {
        return appService.checkVersion(versionCheckRequestDto)
    }
}