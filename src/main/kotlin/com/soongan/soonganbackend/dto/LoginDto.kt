package com.soongan.soonganbackend.dto

import com.soongan.soonganbackend.enums.Provider
import jakarta.validation.constraints.NotNull

data class LoginDto(
    @field:NotNull(message = "provider 정보는 필수입니다.")
    val provider: Provider,

    @field:NotNull(message = "iDToken 정보는 필수입니다.")
    val idToken: String
)