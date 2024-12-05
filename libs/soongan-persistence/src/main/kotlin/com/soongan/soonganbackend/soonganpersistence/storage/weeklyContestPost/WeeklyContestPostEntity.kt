package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost

import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
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
@Table(name = "weekly_contest_post",
    indexes = [
        Index(name = "weekly_contest_post_idx_weekly_contest_id_ranking", columnList = "weekly_contest_id,ranking"),
        Index(name = "weekly_contest_post_idx_member_id", columnList = "member_id")
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class WeeklyContestPostEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(targetEntity = WeeklyContestEntity::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_contest_id")
    val weeklyContest: WeeklyContestEntity,

    @ManyToOne(targetEntity = MemberEntity::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: MemberEntity,

    @Column(name = "image_url", nullable = false)
    val imageUrl: String = "",

    @Column(name = "ranking", nullable = false)
    val ranking: Int = 0,

    @Column(name = "like_count")
    val likeCount: Int = 0,

    @Column(name = "comment_count")
    val commentCount: Int = 0
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
