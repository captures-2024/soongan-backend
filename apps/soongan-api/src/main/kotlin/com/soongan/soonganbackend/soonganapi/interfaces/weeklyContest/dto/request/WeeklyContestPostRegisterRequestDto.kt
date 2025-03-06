package com.soongan.soonganbackend.soonganapi.interfaces.weeklyContest.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import org.springframework.web.multipart.MultipartFile

@Schema(description = "주간 대회 게시글 등록 요청 DTO")
data class WeeklyContestPostRegisterRequestDto(
    @Schema(description = "게시글 제목", required = true)
    @field:NotBlank
    val title: String,

    @Schema(description = "게시글 사진", required = true)
    @field:NotBlank
    val imageFile: MultipartFile
)
