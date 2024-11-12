package com.soongan.soonganbackend.soonganapi.interfaces.fcm

import com.soongan.soonganbackend.soonganapi.interfaces.fcm.dto.request.FcmRegistRequestDto
import com.soongan.soonganbackend.soonganapi.interfaces.fcm.dto.response.FcmTokenInfoResponseDto
import com.soongan.soonganbackend.soonganapi.service.fcm.FcmService
import com.soongan.soonganbackend.soongansupport.domain.UserAgentEnum
import com.soongan.soonganbackend.soongansupport.util.constant.Uri
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
        @RequestHeader(value = "User-Agent") userAgentEnum: UserAgentEnum,
        @RequestBody fcmRegistRequestDto: FcmRegistRequestDto
    ): FcmTokenInfoResponseDto {
        return fcmService.registFcmToken(userAgentEnum, fcmRegistRequestDto)
    }
}
