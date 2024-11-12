package com.soongan.soonganbackend.soonganapi.interfaces.fcm.dto.request

data class FcmMessageDto(
    val message: Message
)

data class Message(
    val token: String,
    val notification: Notification
)

data class Notification(
    val title: String,
    val body: String
)
