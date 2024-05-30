package com.soongan.soonganbackend.persistence.commentLike

import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikeRepository: JpaRepository<CommentLikeEntity, Long> {
}
