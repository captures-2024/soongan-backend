package com.soongan.soonganbackend.soongansupport.util.dto

import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum
import java.time.LocalDateTime

data class FcmMessageDto(
    val message: Message
)

data class Message(
    val token: String,
    val notification: Notification,
    val data: MessageData
)

data class Notification(
    val title: String,
    val body: String,
    val image: String? = null  // FCM 푸시 알림 이미지 (썸네일)
)

data class MessageData(
    val link: String,  // 알림 클릭 시 이동할 링크
    val notificationType: NotificationTypeEnum,
    val postId: Long? = null,  // 게시글 관련 알림일 시 게시글 ID
    val timestamp: LocalDateTime = LocalDateTime.now()  // 알림 발생 시간
)