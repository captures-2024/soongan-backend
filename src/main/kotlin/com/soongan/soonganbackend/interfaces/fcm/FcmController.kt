package com.soongan.soonganbackend.interfaces.fcm

import com.soongan.soonganbackend.enums.UserAgent
import com.soongan.soonganbackend.interfaces.fcm.dto.FcmRegistRequestDto
import com.soongan.soonganbackend.persistence.fcm.FcmTokenEntity
import com.soongan.soonganbackend.service.fcm.FcmService
import com.soongan.soonganbackend.util.common.constant.Uri
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Uri.FCM)
class FcmController(
    private val fcmService: FcmService
) {

    @Operation(summary = "FCM 토큰 정보 등록 Api", description = "FCM 토큰과 디바이스 정보로 FCM 토큰을 등록합니다.")
    @PostMapping
    fun registFcmToken(
        @RequestHeader(value = "User-Agent") userAgent: UserAgent,
        @RequestBody fcmRegistRequestDto: FcmRegistRequestDto
    ): FcmTokenEntity {
        return fcmService.registFcmToken(userAgent, fcmRegistRequestDto)
    }
}