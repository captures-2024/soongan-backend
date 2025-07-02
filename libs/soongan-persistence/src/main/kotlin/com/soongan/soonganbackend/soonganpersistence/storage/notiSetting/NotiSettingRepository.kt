package com.soongan.soonganbackend.soonganpersistence.storage.notiSetting

import org.springframework.data.jpa.repository.JpaRepository

interface NotiSettingRepository: JpaRepository<NotiSettingEntity, Long> {

    fun findByMember_Id(memberId: Long): NotiSettingEntity?
}