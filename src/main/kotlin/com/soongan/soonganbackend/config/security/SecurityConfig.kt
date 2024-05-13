package com.soongan.soonganbackend.config.security

import com.soongan.soonganbackend.service.CustomOAuth2MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig @Autowired constructor(
    private val customOAuth2MemberService: CustomOAuth2MemberService
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
                // 기본 DefaultOAuth2UserService 대신 직접 구현한 CustomOAuth2MemberService 사용
                it.userInfoEndpoint { endpoint ->
                    endpoint.userService(customOAuth2MemberService)
                }
            }
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {  // cors 설정
        val configuration = CorsConfiguration()
        // 우선 모든 요청에 대해 허용
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true  // 인증 정보를 서버로 전송할 수 있도록 허용
        configuration.maxAge = 3600L  // preflight 요청의 결과가 캐시되는 시간 (1시간)

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }
}