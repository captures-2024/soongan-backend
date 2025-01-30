package com.soongan.soonganbackend.soonganapi.interfaces

import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
class ApplicationController(
    private val env: Environment
) {

    companion object {
        private val logger = KotlinLogging.logger { }
    }

    @GetMapping("/_health")
    fun healthCheck(): String {
        logger.info { "Soongan Api Health check" }
        return "Soongan-Api-${env.getProperty("EXECUTION_ENV")}"
    }
}