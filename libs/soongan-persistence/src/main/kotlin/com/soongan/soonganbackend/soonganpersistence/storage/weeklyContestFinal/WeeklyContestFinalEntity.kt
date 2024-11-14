package com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestFinal

import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContest.WeeklyContestEntity
import com.soongan.soonganbackend.soonganpersistence.storage.weeklyContestPost.WeeklyContestPostEntity
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
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "weekly_contest_final",
    indexes = [
        Index(name = "weekly_contest_final_idx_weekly_contest_id_ranking", columnList = "weekly_contest_id,ranking"),
    ]
)
@EntityListeners(AuditingEntityListener::class)
data class WeeklyContestFinalEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(targetEntity = WeeklyContestEntity::class, fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_contest_id")
    val weeklyContest: WeeklyContestEntity,

    @OneToOne(targetEntity = WeeklyContestPostEntity::class)
    @JoinColumn(name = "weekly_contest_post_id")
    val weeklyContestPost: WeeklyContestPostEntity,

    @Column(name = "rank")
    val ranking: Int,

    @Column(name = "score")
    val score: BigDecimal
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
