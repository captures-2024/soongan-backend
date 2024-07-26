package com.soongan.soonganbackend.persistence.member

import com.soongan.soonganbackend.enums.Provider
import com.soongan.soonganbackend.util.common.dto.MemberDetail
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener::class)
data class MemberEntity(
    @Column(name = "email", unique = true, nullable = false)
    val email: String,

    @Column(name = "nickname")
    val nickname: String? = null,

    @Column(name = "birth_date")
    val birthDate: LocalDate? = null,

    @Column(name = "profile_image_url")
    val profileImageUrl: String? = null,

    @Column
    @Enumerated(EnumType.STRING)
    val provider: Provider,

    @Column(name = "authorities")
    val authorities: String,

    @Column(name = "withdrawal_at")
    val withdrawalAt: LocalDateTime? = null
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

    fun toMemberDetails(): MemberDetail {
        return MemberDetail(
            id = id!!,
            email = email,
            nickname = nickname,
            birthDate = birthDate,
            profileImageUrl = profileImageUrl,
            memberAuthorities = authorities.split(",").map { SimpleGrantedAuthority(it.trim()) },
            createdAt = createdAt,
            updatedAt = updatedAt,
            withdrawalAt = withdrawalAt
        )
    }
}
