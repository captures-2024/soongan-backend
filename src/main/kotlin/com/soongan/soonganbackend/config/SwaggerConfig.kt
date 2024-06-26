package com.soongan.soonganbackend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .apiInfo(swaggerInfo()) // api 정보
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.soongan.soonganbackend")) // controller 최상위 package
            .paths(PathSelectors.any())
            .build()
            .useDefaultResponseMessages(false) // swagger에서 제공해주는 응답코드 ( 200,401,403,404 )에 대한 기본 메시지를 제거
    }

    private fun swaggerInfo(): ApiInfo? {
        return ApiInfoBuilder().title("Soongan API Documentation")
            .version("1.0")
            .build()
    }
}
