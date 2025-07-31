package com.soongan.soonganbackend.soonganpersistence.storage.explain

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.notification.NotificationEntity
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "explains")
@EntityListeners(AuditingEntityListener::class)
data class ExplainEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @OneToOne(targetEntity = MemberEntity::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: MemberEntity,

    @Column(name = "target_id")
    val targetId: Long,

    @Column(name = "target_type")
    val targetType: ReportTargetTypeEnum,

    @Column(name = "explains")
    val explains: String
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
