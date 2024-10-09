package com.soongan.soonganbackend.soonganweb.config

import com.soongan.soonganbackend.soonganweb.resolver.LoginMemberArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val loginMemberArgumentResolver: LoginMemberArgumentResolver
): WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(loginMemberArgumentResolver)
    }
}
