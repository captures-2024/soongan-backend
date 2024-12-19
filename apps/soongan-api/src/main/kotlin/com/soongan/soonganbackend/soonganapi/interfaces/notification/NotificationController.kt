package com.soongan.soonganbackend.soonganapi.interfaces.notification

import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.GetNotificationCountResponseDto
import com.soongan.soonganbackend.soonganapi.interfaces.notification.dto.response.GetNotificationResponseDto
import com.soongan.soonganbackend.soonganapi.service.notification.NotificationService
import com.soongan.soonganbackend.soonganpersistence.storage.member.MemberEntity
import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
import com.soongan.soonganbackend.soonganweb.resolver.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
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

    @GetMapping(Uri.COUNT)
    @Operation(summary = "알림 개수 조회 Api", description = "알림 탭 별 유저가 열람하지 않은 알림 개수를 조회합니다.")
    fun countNotifications(@LoginMember loginMember: MemberEntity): List<GetNotificationCountResponseDto> {
        return notificationService.countNotification(loginMember)
    }

    @GetMapping
    @Operation(summary = "알림 목록 조회 Api", description = "알림 탭 별 알림 목록을 조회합니다.")
    fun getNotificationList(@LoginMember loginMember: MemberEntity, @RequestParam type: NotificationTypeEnum): GetNotificationResponseDto {
        return notificationService.getNotifications(loginMember, type)
    }

    @PostMapping
    @Operation(summary = "알림 읽음 처리 Api", description = "알림을 읽음 처리합니다.")
    fun readNotification(@RequestBody notificationId: Long): Long {
        return notificationService.readNotification(notificationId)
    }

    // TODO: 소명 완료 시 삭제?
    @DeleteMapping
    @Operation(summary = "알림 삭제 Api", description = "알림을 삭제합니다.")
    fun deleteNotification(@RequestBody notificationId: Long) {
        notificationService.deleteNotification(notificationId)
    }

}
