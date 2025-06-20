package com.soongan.soonganbackend.soongansupport.util.common

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.toEpochMilli(): Long {
    return this.toInstant(ZoneOffset.UTC).toEpochMilli()
}

fun LocalDateTime.toEpochMilliKST(): Long {
    val zoneId = ZoneId.of("Asia/Seoul")
    return this.atZone(zoneId).toInstant().toEpochMilli()
}