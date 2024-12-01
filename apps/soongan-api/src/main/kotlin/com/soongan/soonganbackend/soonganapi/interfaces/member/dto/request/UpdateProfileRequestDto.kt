package com.soongan.soonganbackend.soonganapi.interfaces.member.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile

data class UpdateProfileRequestDto(
    @field:NotBlank(message = "닉네임은 빈 문자열이거나 null일 수 없습니다. 닉네임을 수정하지 않는다면 요청 필드 자체에서 제외해주세요.")
    val nickname: String? = null,

    @field:NotBlank(message = "자기소개는 빈 문자열이거나 null일 수 없습니다. 자기소개를 수정하지 않는다면 요청 필드 자체에서 제외해주세요.")
    val selfIntroduction: String? = null,

    @field:NotNull(message = "프로필 이미지는 null일 수 없습니다. 프로필 이미지를 수정하지 않는다면 요청 필드 자체에서 제외해주세요.")
    val profileImage: MultipartFile? = null
)
