package com.soongan.soonganbackend.soonganpersistence.config

import com.querydsl.jpa.DefaultQueryHandler
import com.querydsl.jpa.Hibernate5Templates
import com.querydsl.jpa.QueryHandler
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuerydslConfig(
    private val entityManager: EntityManager
) {

    @Bean
    fun jpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(CustomHibernate5Templates(), entityManager)
    }
}

/**
 * To resolve querydsl issues due to Hibernate 6.0 upgrade
 */
class CustomHibernate5Templates(): Hibernate5Templates() {
    override fun getQueryHandler(): QueryHandler? {
        return DefaultQueryHandler.DEFAULT
    }
}