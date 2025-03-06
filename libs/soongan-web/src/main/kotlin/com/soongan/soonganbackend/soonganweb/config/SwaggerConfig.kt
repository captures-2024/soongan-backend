package com.soongan.soonganbackend.soonganweb.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openApi(): OpenAPI {
        val jwtScheme = SecurityScheme()
            .name("JWT")
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("""
                JWT 인증을 위해 아래 절차를 따르세요:
                1. /members/login 엔드포인트로 로그인하여 accessToken과 refreshToken을 획득
                2. 모든 인증 요청의 헤더에 'Authorization: Bearer {accessToken}' 형식으로 추가
                3. 토큰 만료 시 /members/refresh 엔드포인트로 새 토큰 발급
            """.trimIndent())

        val components = Components()
            .addSecuritySchemes("JWT", jwtScheme)

        return OpenAPI()
            .info(apiInfo())
            .components(components)
    }

    fun apiInfo(): Info {
        return Info()
            .title("Soongan API")
            .description("""
                ### 아래 API들은 JWT 인증이 필요하지 않습니다.
                - 로그인 [POST] /members/login
                - JWT 갱신 [PATCH] /members/refresh
                - 주간 콘테스트 게시글 조회 [GET] /weekly/contests/posts
                - FCM 토큰 저장 [POST] /fcm
                - FCM 테스트 [GET} /fcm/test (곧 삭제될 예정)
                
                ### 아래 API들은 JWT 인증 없이도 사용할 수 있지만 응답이 다를 수 있습니다.
                - 홈 화면 조회 [GET] /home
                - 주간 콘테스트 게시글 단일 조회 [GET] /weekly/contests/posts/{postId}
                - 게시글 댓글 조회 [GET] /comments
                - 게시글 대댓글 조회 [GET] /comments/replies
                
                ### 나머지 API들은 JWT 인증이 없다면 401 에러가 발생합니다.
            """.trimIndent())
            .version("1.0.0")
    }
}
