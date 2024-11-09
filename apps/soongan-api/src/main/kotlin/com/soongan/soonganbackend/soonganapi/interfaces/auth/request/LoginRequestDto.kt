package com.soongan.soonganbackend.soonganapi.interfaces.auth.request

import com.soongan.soonganbackend.soongansupport.domain.ProviderEnum
import jakarta.validation.constraints.NotNull

data class LoginRequestDto(
    @field:NotNull(message = "provider 정보는 필수입니다.")
    val provider: ProviderEnum,

    @field:NotNull(message = "iDToken 정보는 필수입니다.")
    val idToken: String,

    @field:NotNull(message = "fcmToken 정보는 필수입니다.")
    val fcmToken: String
)
