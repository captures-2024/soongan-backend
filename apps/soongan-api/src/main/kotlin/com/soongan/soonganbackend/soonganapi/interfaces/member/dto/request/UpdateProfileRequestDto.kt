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
    @field:NotBlank(message = "닉네임은 빈 문자열이거나 null일 수 없습니다. 닉네임을 수정하지 않는다면 요청 필드 자체에서 제외해주세요.")
    val nickname: String? = null,

    @Schema(description = "수정할 자기소개", required = false)
    @field:NotBlank(message = "자기소개는 빈 문자열이거나 null일 수 없습니다. 자기소개를 수정하지 않는다면 요청 필드 자체에서 제외해주세요.")
    val selfIntroduction: String? = null,

    @Schema(description = "수정할 프로필 이미지", required = false)
    @field:NotNull(message = "프로필 이미지는 null일 수 없습니다. 프로필 이미지를 수정하지 않는다면 요청 필드 자체에서 제외해주세요.")
    val profileImage: MultipartFile? = null,

    @Schema(description = "프로필 이미지 삭제 여부", required = false)
    val isDefaultProfileImage: Boolean = false
)
