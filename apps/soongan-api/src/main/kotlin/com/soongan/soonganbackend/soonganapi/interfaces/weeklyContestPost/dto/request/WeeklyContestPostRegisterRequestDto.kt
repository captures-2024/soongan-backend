package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContestPost.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile

data class WeeklyContestPostRegisterRequestDto(
    @field:NotBlank
    val title: String,
    @field:NotNull
    val imageFile: MultipartFile
)
