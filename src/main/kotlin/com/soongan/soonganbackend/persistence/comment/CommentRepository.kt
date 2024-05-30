package com.soongan.soonganbackend.persistence.comment

import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<CommentEntity, Long> {
}
