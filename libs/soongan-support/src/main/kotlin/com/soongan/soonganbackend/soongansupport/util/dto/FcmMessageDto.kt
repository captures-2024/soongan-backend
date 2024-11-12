package com.soongan.soonganbackend.soongansupport.util.dto

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
