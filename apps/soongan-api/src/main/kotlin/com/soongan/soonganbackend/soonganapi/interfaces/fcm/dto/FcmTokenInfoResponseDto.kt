package com.soongan.soonganbackend.soonganapi.interfaces.fcm.dto

import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum


data class FcmTokenInfoResponseDto(
    val id: Long,
    val token: String,
    val deviceId: String,
    val deviceType: UserAgentEnum
)
