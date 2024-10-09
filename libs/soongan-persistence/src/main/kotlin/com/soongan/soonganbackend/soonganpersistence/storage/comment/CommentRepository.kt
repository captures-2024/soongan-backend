package com.soongan.soonganbackend.soonganpersistence.storage.comment

import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<CommentEntity, Long> {
}
