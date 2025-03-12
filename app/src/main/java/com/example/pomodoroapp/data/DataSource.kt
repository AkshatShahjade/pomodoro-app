package com.example.pomodoroapp.data

import com.example.pomodoroapp.R

object DataSource {
    val timerProfileList: List<TimerProfile> = listOf(
        TimerProfile("25/5"),
        TimerProfile("52/17", 52,17, false),
        TimerProfile("25/5/15/4", longBreaksOn = true)
    )
    val breakDurationLimits: ClosedFloatingPointRange<Float> = 1f..60f
    val workDurationLimits: ClosedFloatingPointRange<Float> = 1f..270f
    val sessionsBeforeLongBreakLimits: ClosedFloatingPointRange<Float> = 1f..10f

    val notificationSoundsList: List<Notification> = listOf(
        Notification("1",R.raw.notification_sound_1),
        Notification("2", R.raw.notification_sound_2),
    )
}

