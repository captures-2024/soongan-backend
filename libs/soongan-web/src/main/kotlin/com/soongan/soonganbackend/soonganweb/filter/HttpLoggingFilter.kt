package com.soongan.soonganbackend.soonganweb.filter

import com.soongan.soonganbackend.soongansupport.util.constant.ColorCode
import com.soongan.soonganbackend.soongansupport.util.constant.MdcConstant
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class HttpLoggingFilter: OncePerRequestFilter() {

    private val httpLogger = KotlinLogging.logger { }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val requestTime = MDC.get(MdcConstant.REQUEST_TIME)?.toLong() ?: System.currentTimeMillis()
        val requestUuid = MDC.get(MdcConstant.UUID) ?: "N/A"

        httpLogger.info { "${ColorCode.GREEN}[${MDC.get(MdcConstant.UUID)}]${ColorCode.CYAN}[Request]${ColorCode.RESET} ${request.method} ${request.requestURI}" }

        filterChain.doFilter(request, response)

        val spendTime = System.currentTimeMillis() - requestTime
        httpLogger.info { "${ColorCode.GREEN}[${requestUuid}]${ColorCode.BLUE}[Response]${ColorCode.RESET} status: ${response.status} ${ColorCode.YELLOW}- ${spendTime}ms${ColorCode.RESET}" }
    }
}