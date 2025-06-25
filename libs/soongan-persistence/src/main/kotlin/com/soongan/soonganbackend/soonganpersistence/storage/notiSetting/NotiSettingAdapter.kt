package com.soongan.soonganbackend.soonganpersistence.storage.notiSetting

import org.springframework.stereotype.Component

@Component
class NotiSettingAdapter(
    private val notiSettingRepository: NotiSettingRepository
) {

    fun findByMemberId(memberId: Long): NotiSettingEntity? {
        return notiSettingRepository.findByMember_Id(memberId)
    }

    fun save(notiSettingEntity: NotiSettingEntity): NotiSettingEntity {
        return notiSettingRepository.save(notiSettingEntity)
    }
}