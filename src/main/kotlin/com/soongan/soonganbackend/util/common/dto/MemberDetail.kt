package com.soongan.soonganbackend.util.common.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class MemberDetail(
    val id: Long,
    val email: String,
    val memberAuthorities: Collection<GrantedAuthority>
): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority>  = memberAuthorities.toMutableList()

    override fun getPassword(): String = ""

    override fun getUsername(): String = ""

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
