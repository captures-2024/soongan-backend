package com.soongan.soonganbackend.service.fcm

import com.soongan.soonganbackend.enums.UserAgent
import com.soongan.soonganbackend.interfaces.fcm.dto.FcmRegistRequestDto
import com.soongan.soonganbackend.interfaces.fcm.dto.FcmTokenInfoResponseDto
import com.soongan.soonganbackend.persistence.fcm.FcmTokenAdaptor
import com.soongan.soonganbackend.persistence.fcm.FcmTokenEntity
import com.soongan.soonganbackend.util.common.exception.SoonganException
import com.soongan.soonganbackend.util.common.exception.StatusCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmService(
    private val fcmTokenAdaptor: FcmTokenAdaptor
) {

    @Transactional
    fun registFcmToken(userAgent: UserAgent, fcmRegistRequestDto: FcmRegistRequestDto): FcmTokenInfoResponseDto {
        fcmTokenAdaptor.findByToken(token = fcmRegistRequestDto.token)?.let {
            throw SoonganException(StatusCode.SOONGAN_API_ALREADY_EXIST_FCM_TOKEN)
        }

        val foundFcmTokenByDeviceId = fcmTokenAdaptor.findByDeviceId(deviceId = fcmRegistRequestDto.deviceId)
        if (foundFcmTokenByDeviceId != null) {
            val updatedFcmToken = fcmTokenAdaptor.save(
                foundFcmTokenByDeviceId.copy(
                    token = fcmRegistRequestDto.token
                )
            )

            return FcmTokenInfoResponseDto(
                id = updatedFcmToken.id!!,
                token = updatedFcmToken.token,
                deviceId = updatedFcmToken.deviceId,
                deviceType = updatedFcmToken.deviceType
            )
        }

        val savedFcmToken = fcmTokenAdaptor.save(
            FcmTokenEntity(
                deviceType = userAgent,
                token = fcmRegistRequestDto.token,
                deviceId = fcmRegistRequestDto.deviceId
            )
        )

        return FcmTokenInfoResponseDto(
            id = savedFcmToken.id!!,
            token = savedFcmToken.token,
            deviceId = savedFcmToken.deviceId,
            deviceType = savedFcmToken.deviceType
        )
    }
}