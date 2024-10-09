package com.soongan.soonganbackend.soonganpersistence.storage.commentLike

import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikeRepository: JpaRepository<CommentLikeEntity, Long> {
}
