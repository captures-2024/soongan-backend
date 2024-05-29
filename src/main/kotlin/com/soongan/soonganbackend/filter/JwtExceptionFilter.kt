package com.soongan.soonganbackend.filter

import com.soongan.soonganbackend.exception.jwt.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtExceptionFilter: OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {
            filterChain.doFilter(request, response)
        } catch (exception: JwtException) {
            response.addHeader("Content-Type", "application/json")
            response.characterEncoding = "UTF-8"
            response.writer.write("{\"message\": \"${exception.message}\"}")
        }
    }
}