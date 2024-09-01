package com.soongan.soonganbackend.persistence.fcm

import com.soongan.soonganbackend.enums.UserAgent
import com.soongan.soonganbackend.persistence.member.MemberEntity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "fcm_token",
    indexes = [
        Index(name = "fcm_token_idx_member_id", columnList = "member_id"),
        Index(name = "fcm_token_idx_device_type", columnList = "device_type"),
        Index(name = "fcm_token_idx_token", columnList = "token"),
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class FcmTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "token", nullable = false)
    val token: String,

    @ManyToOne(targetEntity = MemberEntity::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberEntity? = null,

    @Column(name = "device_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val deviceType: UserAgent,

    @Column(name = "device_id", nullable = false)
    val deviceId: String
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
