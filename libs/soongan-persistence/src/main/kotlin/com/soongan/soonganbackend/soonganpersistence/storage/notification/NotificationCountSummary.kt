package com.soongan.soonganbackend.soonganpersistence.storage.notification

import com.soongan.soonganbackend.soongansupport.domain.NotificationTypeEnum

interface NotificationCountSummary {
    fun getType(): NotificationTypeEnum
    fun getNotificationCount(): Long
}
