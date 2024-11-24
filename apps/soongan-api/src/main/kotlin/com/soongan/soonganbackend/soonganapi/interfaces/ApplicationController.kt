package com.soongan.soonganbackend.soonganapi.interfaces

import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApplicationController(
    private val env: Environment
) {

    @GetMapping("/_health")
    fun healthCheck(): String {
        return "Soongan-Api-${env.getProperty("EXECUTION_ENV")}"
    }
}