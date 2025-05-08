package com.soongan.soonganbackend.soonganapi.admin.member.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min

@Schema(description = "회원 정보 조회 어드민 요청 DTO")
data class GetMembersAdminRequestDto(
    @field:Schema(description = "회원 이메일")
    @field:Email(message = "이메일 형식이 올바르지 않습니다.")
    val email: String? = null,

    @field:Schema(description = "회원 이름")
    val nickname: String? = null,

    @field:Schema(description = "페이지")
    @field:Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    val page: Int = 1,

    @field:Schema(description = "페이지 사이즈")
    @field:Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
    val size: Int = 10
)
