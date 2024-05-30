package com.soongan.soonganbackend.persistence.member

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener::class)
data class MemberEntity(
    @Column(name = "email", unique = true, nullable = false)
    val email: String,

    @Column(name = "nickname")
    val nickname: String?,

    @Column(name = "birthDate")
    val birthDate: LocalDate?,

    @Column(name = "profileImageUrl")
    val profileImageUrl: String?,

    @Column(name = "provider")
    val provider: String,

    @Column(name = "authorities")
    val authorities: String,

    @Column(name = "withdrawal_at")
    val withdrawalAt: LocalDateTime?

) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
