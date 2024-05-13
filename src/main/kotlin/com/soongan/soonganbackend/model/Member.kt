package com.soongan.soonganbackend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
data class Member(
    @Column
    val email: String,

    @Column
    val nickname: String,

    @Column
    val birthDate: LocalDate,

    @Column
    val profileImageUrl: String,

    @Column
    val provider: String,

    @Column
    val authorities: List<String>,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
}