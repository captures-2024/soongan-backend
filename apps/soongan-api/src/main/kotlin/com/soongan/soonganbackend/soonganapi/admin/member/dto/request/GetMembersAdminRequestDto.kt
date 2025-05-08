package com.soongan.soonganbackend.soonganapi.admin.member.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import jakarta.validation.constraints.NotNull
import kotlin.reflect.KClass

@Schema(description = "회원 정보 조회 어드민 요청 DTO")
data class GetMembersAdminRequestDto(
    @Schema(description = "조회 기준 (ex. email, nickname 등)")
    @field:ValidSearchBy
    @field:NotNull
    val searchBy: SearchByType,

    @Schema(description = "조회 키워드")
    @field:NotNull
    val searchKeyword: String,
)

enum class SearchByType {
    EMAIL, NICKNAME
}

@MustBeDocumented
@Constraint(validatedBy = [SearchByValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidSearchBy(
    val message: String = "허용되지 않은 검색 조건입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)


class SearchByValidator : ConstraintValidator<ValidSearchBy, String?> {
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        return SearchByType.entries.any { it.name == value }
    }
}