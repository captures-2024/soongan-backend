package com.soongan.soonganbackend.soonganpersistence.storage.comment

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ContestTypeEnum
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
@Table(
    name = "comment",
    indexes = [
        Index(name = "comment_idx_member_id_contest_type", columnList = "member_id_contest_type"),
        Index(name = "comment_uidx_post_id_contest_type", columnList = "post_id,contest_type", unique = true),
    ])
@EntityListeners(AuditingEntityListener::class)
data class CommentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(targetEntity = MemberEntity::class)
    @JoinColumn(name = "member_id")
    val member: MemberEntity,

    @Column(name = "post_id", nullable = false)
    val postId: Long,

    @Column(name = "contest_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val contestType: ContestTypeEnum,

    @Column(name = "comment_text")
    val commentText: String = "",

    @ManyToOne(targetEntity = CommentEntity::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    val parentComment: CommentEntity?,

    @Column(name = "comment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    val commentStatus: CommentStatusEnum = CommentStatusEnum.ACTIVE,

    @Column(name = "like_count")
    val likeCount: Int = 0,

    @Column(name = "is_blind")
    val isBlind: Boolean = false
    ) {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        private set

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
        private set
}
