package com.soongan.soonganbackend.interfaces.weeklyContest.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.web.multipart.MultipartFile

data class WeeklyContestPostRegisterRequestDto(
    @field:Min(1)
    val weeklyContestRound: Int,
    @field:NotBlank
    val subject: String,
    @field:NotBlank
    val imageFile: MultipartFile
)
