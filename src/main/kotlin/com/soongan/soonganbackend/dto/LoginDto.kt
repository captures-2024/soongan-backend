package com.soongan.soonganbackend.dto

import jakarta.validation.constraints.NotNull

data class LoginDto(
    @NotNull
    val provider: String,

    @NotNull
    val accessToken: String
)