package com.soongan.soonganbackend.interfaces.fcm.dto

import com.soongan.soonganbackend.enums.UserAgent

data class FcmTokenInfoResponseDto(
    val id: Long,
    val token: String,
    val deviceId: String,
    val deviceType: UserAgent
)