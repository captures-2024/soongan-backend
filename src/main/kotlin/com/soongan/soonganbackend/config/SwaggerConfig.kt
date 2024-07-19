package com.soongan.soonganbackend.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
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
            .components(Components())
            .info(apiInfo())
            .addSecurityItem(securityRequirement)
            .components(components)
    }

    fun apiInfo(): Info {
        return Info()
            .title("Soongan API")
            .description("로그인, Jwt 리프레쉬 Api 외에는 로그인시 발급되는 Authorization Bearer Token이 필요합니다.")
            .version("1.0.0")
    }
}