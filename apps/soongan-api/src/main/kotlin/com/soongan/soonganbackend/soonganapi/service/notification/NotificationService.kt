package com.soongan.soonganbackend.soonganapi.service.notification

import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.request.UpdateNotiSettingRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.NotiSettingResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.GetNotificationCountResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.GetNotificationResponseDto
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soonganpersistence.storage.notiSetting.NotiSettingAdapter
import com.soongan.soonganbackend.soonganpersistence.storage.notiSetting.NotiSettingEntity
import com.soongan.soonganbackend.soonganpersistence.storage.notification.NotificationAdapter
import com.soongan.soonganbackend.soongansupport.domain.NotificationSubTypeEnum
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import com.soongan.soonganbackend.soongansupport.util.exception.SoonganException
import com.soongan.soonganbackend.soongansupport.util.exception.StatusCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NotificationService(
    private val notificationAdapter: NotificationAdapter,
    private val notiSettingAdapter: NotiSettingAdapter
) {

    fun countNotification(loginMember: MemberEntity): List<GetNotificationCountResponseDto> {
        return notificationAdapter.countNotification(loginMember).map {
            GetNotificationCountResponseDto(it.getNotificationCount().toInt(), it.getType())
        }
    }

    fun getNotifications(loginMember: MemberEntity, type: NotificationTypeEnum): GetNotificationResponseDto {
        val notifications = notificationAdapter.getNotificationByType(loginMember, type)
        return GetNotificationResponseDto.from(type, notifications)
    }

    fun readNotification(loginMember: MemberEntity, notificationId: Long): Long {
        val notification = notificationAdapter.getByIdOrNull(notificationId)
            ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_NOTIFICATION, "해당 id로 조회되는 알림이 없습니다.")

        if (notification.member.id != loginMember.id) {
            throw SoonganException(StatusCode.SOONGAN_API_NOT_LOGIN_MEMBER_NOTIFICATION, "해당 알림은 로그인한 유저의 알림이 아닙니다.")
        }

        if (notification.isRead) {
            throw SoonganException(StatusCode.SOONGAN_API_ALREADY_READ_NOTIFICATION, "이미 읽은 알림입니다.")
        }

        val savedEntity = notificationAdapter.save(notification.copy(isRead = true))

        return savedEntity.id!!
    }

    @Transactional
    fun deleteNotification(loginMember: MemberEntity, notificationId: Long): Unit {
        val notification = notificationAdapter.getByIdOrNull(notificationId)
            ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_NOTIFICATION, "해당 id로 조회되는 알림이 없습니다.")

        if (notification.member.id != loginMember.id) {
            throw SoonganException(StatusCode.SOONGAN_API_NOT_LOGIN_MEMBER_NOTIFICATION, "해당 알림은 로그인한 유저의 알림이 아닙니다.")
        }

        if (notification.subType == NotificationSubTypeEnum.APPEAL) {
            throw SoonganException(StatusCode.SOONGAN_API_CANNOT_DELETE_REPORT_CLARIFICATION, "소명 알림은 삭제할 수 없습니다.")
        }

        notificationAdapter.delete(notification)
    }

    @Transactional
    fun getNotiSetting(loginMember: MemberEntity): NotiSettingResponseDto {
        val notiSetting = notiSettingAdapter.findByMemberId(loginMember.id)
        return if (notiSetting == null) {
            // 알림 설정이 없으면 새로 생성
            val createdNotiSetting = notiSettingAdapter.save(
                NotiSettingEntity(
                    member = loginMember,
                    contestPush = false,
                    activityPush = false,
                    noticePush = false
                )
            )
            NotiSettingResponseDto.from(createdNotiSetting)
        } else {
            NotiSettingResponseDto.from(notiSetting)
        }
    }

    fun updateNotiSetting(loginMember: com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity, requestDto: com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.request.UpdateNotiSettingRequestDto): com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.NotiSettingResponseDto {
        val notiSetting = notiSettingAdapter.findByMemberId(loginMember.id)
            ?: throw SoonganException(StatusCode.SOONGAN_API_NOT_FOUND_NOTI_SETTING, "알림 설정이 존재하지 않습니다.")

        val updatedNotiSetting = notiSetting.copy(
            contestPush = requestDto.contestPush ?: notiSetting.contestPush,
            activityPush = requestDto.activityPush ?: notiSetting.activityPush,
            noticePush = requestDto.noticePush ?: notiSetting.noticePush
        )

        val savedNotiSetting = notiSettingAdapter.save(updatedNotiSetting)

        return NotiSettingResponseDto.from(savedNotiSetting)
    }

}
