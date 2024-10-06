package com.soongan.soonganbackend.soonganpersistence.storage.persistence.commentLike

import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikeRepository: JpaRepository<CommentLikeEntity, Long> {
}
