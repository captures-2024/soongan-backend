package com.soongan.soonganbackend.soonganpersistence.storage.userBanHistory

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "user_ban_history")
@EntityListeners(AuditingEntityListener::class)
data class UserBanHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(targetEntity = MemberEntity::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberEntity,

    @Column(name = "ban_until", nullable = false)
    val banUntil: LocalDateTime,

    @Column(name = "reason", nullable = false)
    val reason: String
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

