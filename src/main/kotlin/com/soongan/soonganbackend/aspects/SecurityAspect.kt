package com.soongan.soonganbackend.aspects

import com.soongan.soonganbackend.persistence.member.MemberAdapter
import com.soongan.soonganbackend.persistence.member.MemberEntity
import com.soongan.soonganbackend.util.common.dto.MemberDetail
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@Aspect
@Component
class SecurityAspect(
    private val memberAdapter: MemberAdapter
) {

    @Around("@annotation(CheckMember) && execution(* com.soongan.soonganbackend.interfaces.*..*Controller.*(..))")
    fun checkMember(joinPoint: ProceedingJoinPoint): Any? {
        val memberDetail = SecurityContextHolder.getContext().authentication.principal
        if (memberDetail is MemberDetail) {
            val member = memberAdapter.getByEmail(memberDetail.email)
                ?: throw RuntimeException("Member not found")

            val method = (joinPoint.signature as MethodSignature).method
            val parameters = method.parameters
            val args = joinPoint.args.toMutableList()

            for (i in parameters.indices) {
                if (parameters[i].type == MemberEntity::class.java) {
                    args[i] = member
                    break
                }
            }

            return joinPoint.proceed(args.toTypedArray())
        }
        return joinPoint.proceed()
    }
}

@Target(FUNCTION)
@Retention(RUNTIME)
annotation class CheckMember