package com.soongan.soonganbackend.soongansupport.util.dto

data class FcmMessageDto(
    val message: Message
)

data class Message(
    val token: String,
    val notification: Notification,
    val data: Map<String, String>
)
data class Notification(
    val title: String,
    val body: String,
    val image: String? = null  // FCM 푸시 알림 이미지 (썸네일)
)