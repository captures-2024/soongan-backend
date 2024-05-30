package com.soongan.soonganbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class SoonganBackendApplication

fun main(args: Array<String>) {
    runApplication<SoonganBackendApplication>(*args)
}
