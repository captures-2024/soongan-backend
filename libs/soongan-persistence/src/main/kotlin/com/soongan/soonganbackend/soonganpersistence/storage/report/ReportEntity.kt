package com.soongan.soonganbackend.soonganpersistence.storage.report

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.ReportTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.ReportTargetTypeEnum
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(
    name = "report",
    indexes = [
        Index(name = "report_idx_report_member_id", columnList = "report_member_id"),
        Index(name = "report_idx_target_member_id", columnList = "target_member_id")
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class ReportEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(targetEntity = MemberEntity::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "report_member_id", nullable = false)
    val reportMember: MemberEntity,

    @ManyToOne(targetEntity = MemberEntity::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "target_member_id", nullable = false)
    val targetMember: MemberEntity,

    @Column(name = "target_id", nullable = false)
    val targetId: Long,

    @Column(name = "target_type", nullable = false)
    val targetType: ReportTargetTypeEnum,

    @Column(name = "report_type", nullable = false)
    val reportType: ReportTypeEnum,

    @Column(name = "reason", nullable = true)
    val reason: String? = null  // reportType이 도용이거나 기타인 경우에만 필수
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
