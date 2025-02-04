package com.soongan.soonganbackend.soonganweb.filter

import com.soongan.soonganbackend.soongansupport.util.constant.MdcConstant
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class MdcContextFilter: OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val requestUuid = UUID.randomUUID().toString()
        MDC.put(MdcConstant.CLIENT_IP, request.remoteAddr)
        MDC.put(MdcConstant.URL_PATH, request.requestURI)
        MDC.put(MdcConstant.HTTP_METHOD, request.method)
        MDC.put(MdcConstant.UUID, requestUuid)
        MDC.put(MdcConstant.REQUEST_TIME, System.currentTimeMillis().toString())

        try {
            filterChain.doFilter(request, response)
        } finally {
            MDC.clear()
        }

    }
}
