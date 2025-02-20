package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.response

data class WeeklyContestPostRegisterResponseDto(
    val postId: Long,
    val title: String,
    val imageUrl: String,
    val registerNickname: String
)
