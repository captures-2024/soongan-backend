package com.soongan.soonganbackend.interfaces.fcm.dto

data class FcmMessageDto(
    val message: Message
)

data class Message(
    val token: String,
    val notification: NotificationDto
)

data class NotificationDto(
    val title: String,
    val body: String
)
