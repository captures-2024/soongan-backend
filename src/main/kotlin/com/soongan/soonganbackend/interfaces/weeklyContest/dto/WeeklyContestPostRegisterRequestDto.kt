package com.soongan.soonganbackend.interfaces.weeklyContest.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class WeeklyContestPostRegisterRequestDto(
    @field:Min(1)
    val weeklyContestRound: Int,
    @field:NotBlank
    val subject: String,
    @field:NotBlank
    val content: String,
    @field:NotBlank
    val imageUrl: String,
)
