package com.soongan.soonganbackend.soonganpersistence.storage.notification

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.NotificationSubTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

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

    @Column(name = "type", nullable = false)
    val type: NotificationTypeEnum,

    @Column(name = "sub_type", nullable = false)
    val subType: NotificationSubTypeEnum,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "body", nullable = false)
    val body: String,

    @Column(name = "redirect_url")
    val redirectUrl: String? = null,

    @Column(name = "is_read", nullable = false)
    val isRead: Boolean = false
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
