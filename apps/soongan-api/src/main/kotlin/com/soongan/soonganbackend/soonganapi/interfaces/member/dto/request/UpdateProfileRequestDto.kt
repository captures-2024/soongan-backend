package com.soongan.soonganbackend.soonganapi.interfaces.member.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile

@Schema(description = "프로필 수정 요청 DTO")
data class UpdateProfileRequestDto(
    @Schema(description = "수정할 닉네임", required = false)
    @field:Size(min = 3, max = 10, message = "닉네임은 3자 이상 10자 이하여야 합니다")
    val nickname: String? = null,

    @Schema(description = "수정할 자기소개", required = false)
    val selfIntroduction: String? = null,

    @Schema(description = "수정할 프로필 이미지", required = false)
    val profileImage: MultipartFile? = null,

    @Schema(description = "프로필 이미지 삭제 여부", required = false)
    val isDefaultProfileImage: Boolean = false
)
