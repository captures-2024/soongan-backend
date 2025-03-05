package com.soongan.soonganbackend.soonganapi.interfaces.auth.dto.request

import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "로그인 요청 DTO")
data class LoginRequestDto(
    @Schema(description = "로그인 제공자 서비스", example = "GOOGLE", required = true)
    @field:NotBlank(message = "provider 정보는 필수입니다.")
    val provider: ProviderEnum,

    @Schema(description = "ID Token", required = true)
    @field:NotBlank(message = "iDToken 정보는 필수입니다.")
    val idToken: String,

    @Schema(description = "해당 기기 FCM Token", required = true)
    @field:NotBlank(message = "fcmToken 정보는 필수입니다.")
    val fcmToken: String
)
