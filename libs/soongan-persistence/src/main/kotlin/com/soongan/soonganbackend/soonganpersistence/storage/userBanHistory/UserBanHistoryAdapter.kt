package com.soongan.soonganbackend.soonganpersistence.storage.userBanHistory

import org.springframework.stereotype.Component

@Component
class UserBanHistoryAdapter(
    private val userBanHistoryRepository: UserBanHistoryRepository
)