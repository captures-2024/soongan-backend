package com.soongan.soonganbackend.soonganpersistence.storage.persistence.postLike

import com.soongan.soonganbackend.soonganpersistence.storage.persistence.comment.ContestTypeEnum
import com.soongan.soonganbackend.soonganpersistence.storage.persistence.member.MemberEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "post_like",
    indexes = [
        Index(name = "post_like_idx_member_id", columnList = "member_id"),
        Index(name = "post_like_uidx_contest_type_post_id", columnList = "contest_type,post_id", unique = true)
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class PostLikeEntity(

    @ManyToOne(targetEntity = MemberEntity::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberEntity,

    @Column(name = "post_id")
    val postId: Long,

    @Column(name = "contest_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val contestType: ContestTypeEnum
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        private set

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
        private set
}
