package com.soongan.soonganbackend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "member")
@EnableJpaAuditing
data class MemberEntity(
    @Column(unique = true, nullable = false)
    val email: String,

    @Column
    val nickname: String?,

    @Column
    val birthDate: LocalDate?,

    @Column
    val profileImageUrl: String?,

    @Column
    val provider: String,

    @Column
    val authorities: List<String>,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set
}