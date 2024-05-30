package com.soongan.soonganbackend.persistence.postLike

import org.springframework.data.jpa.repository.JpaRepository

interface PostLikeRepository: JpaRepository<PostLikeEntity, Long> {
}
