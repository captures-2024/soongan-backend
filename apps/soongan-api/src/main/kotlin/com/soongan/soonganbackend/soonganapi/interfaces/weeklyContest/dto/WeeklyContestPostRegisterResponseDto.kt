package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto

data class WeeklyContestPostRegisterResponseDto(
    val postId: Long,
    val subject: String,
    val imageUrl: String,
    val registerNickname: String
)
