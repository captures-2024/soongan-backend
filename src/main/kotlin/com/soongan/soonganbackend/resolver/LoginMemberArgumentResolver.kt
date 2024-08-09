package com.soongan.soonganbackend.resolver

import com.soongan.soonganbackend.persistence.member.MemberAdapter
import com.soongan.soonganbackend.persistence.member.MemberEntity
import com.soongan.soonganbackend.util.common.dto.MemberDetail
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
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
        return parameter.hasParameterAnnotation(LoginMember::class.java)
    }

    override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer?, webRequest: NativeWebRequest, binderFactory: WebDataBinderFactory?): MemberEntity? {
        val authentication = SecurityContextHolder.getContext().authentication
        val memberDetail = authentication.principal as MemberDetail
        println(memberDetail)
        return memberAdapter.getByEmail(memberDetail.email) ?: throw SoonganException(StatusCode.NOT_FOUND_MEMBER_BY_EMAIL)
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginMember