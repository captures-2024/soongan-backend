package com.soongan.soonganbackend.interfaces.weeklyContest.dto

data class WeeklyContestPostRegisterResponseDto(
    val postId: Long,
    val subject: String,
    val content: String,
    val imageUrl: String,
    val registerNickname: String
)
