package com.soongan.soonganbackend.soonganpersistence.storage.notification

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "notification")
@EntityListeners(AuditingEntityListener::class)
data class NotificationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(targetEntity = MemberEntity::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberEntity,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "body", nullable = false)
    val body: String
)