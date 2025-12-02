package com.soongan.soonganbackend.soonganapi.interfaces.app.dto.request

import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "앱 버전 체크 요청 DTO")
data class VersionCheckRequestDto(
    @Schema(description = "유저 플랫폼 (ANDROID, IOS)", example = "IOS", required = true)
    @field:NotNull(message = "userAgent 정보는 필수입니다.")
    val userAgent: UserAgentEnum,

    @Schema(description = "현재 앱 버전 (semantic versioning: major.minor.patch)", example = "1.0.0", required = true)
    @field:NotBlank(message = "appVersion 정보는 필수입니다.")
    val appVersion: String
)