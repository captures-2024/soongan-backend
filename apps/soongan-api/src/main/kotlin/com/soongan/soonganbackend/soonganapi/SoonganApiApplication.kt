package com.soongan.soonganbackend.soonganapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = ["com.soongan.soonganbackend"])
class SoonganApiApplication

fun main(args: Array<String>) {
    runApplication<SoonganApiApplication>(*args)
}
