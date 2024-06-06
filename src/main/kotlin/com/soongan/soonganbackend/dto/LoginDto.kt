package com.soongan.soonganbackend.dto

import com.soongan.soonganbackend.enums.Provider
import jakarta.validation.constraints.NotNull

data class LoginDto(
    @NotNull
    val provider: Provider,

    @NotNull
    val idToken: String
)