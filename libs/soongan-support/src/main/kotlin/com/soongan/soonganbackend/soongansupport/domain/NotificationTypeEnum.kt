package com.soongan.soonganbackend.soongansupport.domain

enum class NotificationTypeEnum {
    CONTEST,
    ACTIVITY,
    NOTICE
    ;
}

enum class NotificationSubTypeEnum(
    val type: NotificationTypeEnum
){
    CONTEST_START(NotificationTypeEnum.CONTEST),
    CONTEST_END(NotificationTypeEnum.CONTEST),

    COMMENT(NotificationTypeEnum.ACTIVITY),
    LIKE(NotificationTypeEnum.ACTIVITY),
    APPEAL(NotificationTypeEnum.ACTIVITY),

    NOTICE(NotificationTypeEnum.NOTICE)
    ;
}
