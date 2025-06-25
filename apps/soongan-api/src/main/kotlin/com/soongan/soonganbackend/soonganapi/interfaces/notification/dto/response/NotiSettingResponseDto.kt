package com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response

import com.soongan.soonganbackend.soonganpersistence.storage.notiSetting.NotiSettingEntity
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "알림 설정 조회 응답 DTO")
data class NotiSettingResponseDto(
    @Schema(description = "대회 알림 설정", required = true)
    val contestPush: Boolean,

    @Schema(description = "활동 알림 설정", required = true)
    val activityPush: Boolean,

    @Schema(description = "공지 알림 설정", required = true)
    val noticePush: Boolean
) {
    companion object {
        fun from(notiSettingEntity: NotiSettingEntity): NotiSettingResponseDto {
            return NotiSettingResponseDto(
                contestPush = notiSettingEntity.contestPush,
                activityPush = notiSettingEntity.activityPush,
                noticePush = notiSettingEntity.noticePush
            )
        }
    }
}
