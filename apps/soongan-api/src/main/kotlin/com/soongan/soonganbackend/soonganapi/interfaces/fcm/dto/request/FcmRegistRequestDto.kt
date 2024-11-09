package com.soongan.soonganbackend.soonganapi.interfaces.fcm.dto.request

import jakarta.validation.constraints.NotNull

data class FcmRegistRequestDto(
    @field:NotNull
    val token: String,
    @field:NotNull
    val deviceId: String
)
