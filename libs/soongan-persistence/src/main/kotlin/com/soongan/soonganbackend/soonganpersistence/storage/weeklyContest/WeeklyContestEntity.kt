package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(
    name = "weekly_contest",
    indexes = [
        Index(name = "weekly_contest_idx_round", columnList = "round"),
        Index(name = "weekly_contest_idx_start_at_end_at", columnList = "start_at,end_at"),
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class WeeklyContestEntity(

    @Column(name = "round", nullable = false)
    val round: Int = 0,

    @Column(name = "subject", nullable = false)
    val subject: String = "",

    @Column(name = "max_post_allowed", nullable = false)
    val maxPostAllowed: Int = 0,

    @Column(name = "start_at", nullable = false)
    val startAt: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "end_at", nullable = false)
    val endAt: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "vote_start_at", nullable = false)
    val voteStartAt: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "vote_end_at", nullable = false)
    val voteEndAt: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "announced_at", nullable = false)
    val announcedAt: LocalDateTime = LocalDateTime.MIN
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
