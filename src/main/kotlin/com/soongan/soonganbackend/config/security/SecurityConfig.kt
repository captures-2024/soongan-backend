package com.soongan.soonganbackend.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .httpBasic {  // OAuth2 인증을 사용, 필요없는 기본 인증을 비활성화
                it.disable()
            }
            .formLogin {  // OAuth2 인증을 사용, 필요없는 폼 로그인을 비활성화
                it.disable()
            }
            .authorizeHttpRequests {  // 요청에 대한 인증 요구 여부 설정
                it.anyRequest().authenticated()  // 일단 모든 요청에 대해 인증 요구
            }
            .oauth2Login {  // OAuth2 인증 설정
            }
            .build()
    }
}