package com.soongan.soonganbackend.soonganapi.interfaces.app.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "앱 버전 체크 응답 DTO")
data class VersionCheckResponseDto(
    @Schema(description = "최신 버전인지 여부", example = "true")
    val isLatest: Boolean,

    @Schema(description = "현재 앱 버전", example = "1.0.0")
    val currentVersion: String,

    @Schema(description = "최신 버전", example = "1.2.0")
    val latestVersion: String,

    @Schema(description = "강제 업데이트 필요 여부", example = "false")
    val forceUpdate: Boolean,

    @Schema(description = "업데이트 메시지 (필요시)", example = "새로운 기능이 추가되었습니다. 업데이트를 진행해주세요.", nullable = true)
    val updateMessage: String? = null
)