package com.soongan.soonganbackend.soonganpersistence.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(basePackages = ["com.soongan.soonganbackend.soonganpersistence.storage"])
@EnableJpaAuditing
@Configuration
@EntityScan(basePackages = ["com.soongan.soonganbackend.soonganpersistence.storage"])
class JpaConfig {
}
