package com.soongan.soonganbackend.soonganpersistence.storage.explain

import org.springframework.data.jpa.repository.JpaRepository

interface ExplainRepository: JpaRepository<ExplainEntity, Long> {
}