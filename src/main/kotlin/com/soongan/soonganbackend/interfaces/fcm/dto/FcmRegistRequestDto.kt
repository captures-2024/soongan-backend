package com.soongan.soonganbackend.interfaces.fcm.dto

import jakarta.validation.constraints.NotNull

data class FcmRegistRequestDto(
    @field:NotNull
    val token: String,
    @field:NotNull
    val deviceId: String
)
