package com.soongan.soonganbackend.soonganpersistence.storage.notiMember

import org.springframework.data.jpa.repository.JpaRepository

interface NotiMemberRepository: JpaRepository<NotiMemberEntity, Long> {
}
