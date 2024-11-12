package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile

data class WeeklyContestPostRegisterRequestDto(
    @field:Min(1)
    val weeklyContestRound: Int,
    @field:NotBlank
    val subject: String,
    @field:NotNull
    val imageFile: MultipartFile
)
