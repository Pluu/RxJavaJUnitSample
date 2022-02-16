package com.pluu.sample.rxjavajunit

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun printNow(message: Any) {
    val localDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    println("[${"%02d:02d:02d.04d".format(localDateTime.hour, localDateTime.minute, localDateTime.second, localDateTime.nanosecond)}] $message")
}