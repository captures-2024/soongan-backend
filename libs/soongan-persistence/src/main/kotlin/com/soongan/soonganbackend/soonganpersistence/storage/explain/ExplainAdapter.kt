package com.soongan.soonganbackend.soonganpersistence.storage.explain

import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class ExplainAdapter(
    private val explainRepository: ExplainRepository
) {

    @Transactional
    fun save(explainEntity: ExplainEntity): ExplainEntity {
        return explainRepository.save(explainEntity)
    }
}