package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto

data class WeeklyContestPostRegisterResponseDto(
    val postId: Long,
    val subject: String,
    val imageUrl: String,
    val registerNickname: String
)
