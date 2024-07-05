package com.soongan.soonganbackend.config

import com.soongan.soonganbackend.filter.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val jwtFilter: JwtFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors {
                it.configurationSource(corsConfigurationSource())  // 아래에서 정의한 cors 설정을 사용
            }
            .csrf {
                it.disable()  // CSRF 보안 기능 비활성화 -> api 서버는 CSRF 공격에 취약하지 않음
            }
            .sessionManagement {
                // 인증에는 JWT 토큰을 사용하므로 세션을 생성하지 않도록 설정
                // STATELESS: 세션을 생성하지도 않고, 있어도 사용하지 않음
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {  // 요청에 대한 인증 요구 여부 설정
                it.requestMatchers(*passUrls().toTypedArray()).permitAll()  // passUrls에 있는 url은 인증 요구하지 않음
                    .anyRequest().authenticated()  // 일단 모든 요청에 대해 인증 요구
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java) // JwtFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
            // UsernamePasswordAuthenticationFilter는 UsernamePasswordAuthenticationToken을 생성하는 역할 -> JwtFilter에서 대신 Token 생성
            // UsernamePasswordAuthenticationToken은 SecurityContext에 저장되어 인증 정보를 유지하는 역할
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {  // cors 설정
        val configuration = CorsConfiguration()
        // TODO: CORS 설정 클라이언트에 맞게 변경 필요
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true  // 인증 정보를 서버로 전송할 수 있도록 허용
        configuration.maxAge = 3600L  // preflight 요청의 결과가 캐시되는 시간 (1시간)

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }

    fun passUrls(): List<String> {
        return listOf(
            "/members/login",
            "/members/refresh",
            "/api-docs",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**"
        )
    }
}