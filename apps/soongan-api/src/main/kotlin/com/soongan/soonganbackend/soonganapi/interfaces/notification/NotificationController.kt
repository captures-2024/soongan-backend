package com.soongan.soonganbackend.soonganapi.interfaces.notification

import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.request.UpdateNotiSettingRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.NotiSettingResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.GetNotificationCountResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.GetNotificationResponseDto
import com.soongan.soonganbackend.soonganapi.service.notification.NotificationService
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Uri.NOTIFICATIONS)
@Tag(name = "Notification Apis", description = "알림 조회/삭제 API")
class NotificationController(
    private val notificationService: NotificationService
) {

    @GetMapping(Uri.UNREAD_COUNT)
    @Operation(
        summary = "알림 개수 조회 Api",
        description = "알림 탭 별 유저가 열람하지 않은 알림 개수를 조회합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    fun countNotifications(@LoginMember loginMember: MemberEntity): List<GetNotificationCountResponseDto> {
        return notificationService.countNotification(loginMember)
    }

    @GetMapping
    @Operation(
        summary = "알림 목록 조회 Api",
        description = "알림 탭 별 알림 목록을 조회합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    fun getNotificationList(@LoginMember loginMember: MemberEntity, @RequestParam type: NotificationTypeEnum): GetNotificationResponseDto {
        return notificationService.getNotifications(loginMember, type)
    }

    @PostMapping("/{notificationId:[0-9]+}/read")
    @Operation(
        summary = "알림 읽음 처리 Api",
        description = "알림을 읽음 처리합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    fun readNotification(@LoginMember loginMember: MemberEntity, @PathVariable("notificationId") notificationId: Long): Long {
        return notificationService.readNotification(loginMember, notificationId)
    }

    // TODO: 소명 완료 시 삭제?
    @DeleteMapping("/{notificationId:[0-9]+}")
    @Operation(
        summary = "알림 삭제 Api",
        description = "알림을 삭제합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    fun deleteNotification(@LoginMember loginMember: MemberEntity, @PathVariable("notificationId") notificationId: Long) {
        notificationService.deleteNotification(loginMember, notificationId)
    }

    @GetMapping(Uri.SETTINGS)
    @Operation(
        summary = "알림 설정 조회 Api",
        description = "유저의 알림 설정을 조회합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    fun getNotiSetting(@LoginMember loginMember: MemberEntity): NotiSettingResponseDto {
        return notificationService.getNotiSetting(loginMember)
    }

    @PatchMapping(Uri.SETTINGS)
    @Operation(
        summary = "알림 설정 변경 Api",
        description = "유저의 알림 설정을 변경합니다.",
        security = [SecurityRequirement(name = "JWT")]
    )
    fun updateNotiSetting(
        @LoginMember loginMember: MemberEntity,
        @RequestBody requestDto: UpdateNotiSettingRequestDto
    ): NotiSettingResponseDto {
        return notificationService.updateNotiSetting(loginMember, requestDto)
    }
}
