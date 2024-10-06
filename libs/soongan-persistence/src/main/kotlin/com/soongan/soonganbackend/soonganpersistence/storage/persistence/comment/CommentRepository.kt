package com.soongan.soonganbackend.soonganpersistence.storage.persistence.comment

import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<CommentEntity, Long> {
}
