package com.soongan.soonganbackend.aspects

import com.soongan.soonganbackend.persistence.member.MemberAdapter
import com.soongan.soonganbackend.persistence.member.MemberEntity
import com.soongan.soonganbackend.util.common.dto.MemberDetail
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@Aspect
@Component
class SecurityAspect(
    private val memberAdapter: MemberAdapter
) {

    @Before("@annotation(CheckMember)")
    fun checkMember() {
        val memberDetail = SecurityContextHolder.getContext().authentication.principal
        if (memberDetail is MemberDetail) {
            val member = memberAdapter.getByEmail(memberDetail.email)
                ?: throw RuntimeException("Member not found")
            val request = RequestContextHolder.currentRequestAttributes()
            request.setAttribute("member", member, RequestAttributes.SCOPE_REQUEST)
        }
    }
}

@Target(FUNCTION)
@Retention(RUNTIME)
annotation class CheckMember

fun getMemberFromRequest(): MemberEntity {
    val request = RequestContextHolder.currentRequestAttributes()
    return request.getAttribute("member", RequestAttributes.SCOPE_REQUEST) as MemberEntity
}