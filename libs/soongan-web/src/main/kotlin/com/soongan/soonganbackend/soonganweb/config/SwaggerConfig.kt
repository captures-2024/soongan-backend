package com.soongan.soonganbackend.soonganweb.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openApi(): OpenAPI {
        val securityRequirement = SecurityRequirement().addList("JWT")
        val components = Components().addSecuritySchemes("JWT", SecurityScheme()
            .name("JWT")
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
        )

        return OpenAPI()
            .addServersItem(Server().url("/api"))
            .components(Components())
            .info(apiInfo())
            .addSecurityItem(securityRequirement)
            .components(components)
    }

    fun apiInfo(): Info {
        return Info()
            .title("Soongan API")
            .description("""
                ### 아래 Api들을 제외하면 모두 JWT 인증이 필요합니다.
                - 로그인 [POST] /members/login
                - JWT 갱신 [PATCH] /members/refresh
                - FCM 토큰 저장 [POST] /fcm
                - 주간 콘테스트 게시글 조회 [GET] /weekly/contests/posts
            """.trimIndent())
            .version("1.0.0")
    }
}
