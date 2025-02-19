package com.soongan.soonganbackend.soonganweb.resolver

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganUnauthorizedException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class LoginMemberArgumentResolver(
    private val memberAdapter: MemberAdapter
): HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val hasParamAnnotation = parameter.hasParameterAnnotation(LoginMember::class.java)
        val isValidParamType = MemberEntity::class.java.isAssignableFrom(parameter.parameterType)
        return hasParamAnnotation && isValidParamType
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): MemberEntity? {
        val authentication = SecurityContextHolder.getContext().authentication
        val annotation: LoginMember = parameter.getParameterAnnotation(LoginMember::class.java)!!

        val email = authentication.principal as String
        val loginMember: MemberEntity? = memberAdapter.getByEmail(email)

        if (annotation.throwIfUnauthorized && loginMember == null) {
            throw SoonganUnauthorizedException(StatusCode.UNAUTHORIZED)
        }

        return loginMember
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Schema(hidden = true)
annotation class LoginMember(
    val throwIfUnauthorized: Boolean = true
)
