package com.soongan.soonganbackend.soonganconsumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.soongan.soonganbackend"])
class SoonganConsumerApplication

fun main(args: Array<String>) {
    runApplication<SoonganConsumerApplication>(*args)
}
