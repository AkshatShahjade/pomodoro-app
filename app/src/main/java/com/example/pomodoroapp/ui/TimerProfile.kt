package com.example.pomodoroapp.ui

data class TimerProfile(
    val name: String = "25/5",
    val workDuration: Int = 25,
    val breakDuration: Int = 5,
    val longBreaksOn: Boolean = false,
    val longBreakDuration: Int = 15,
    val sessionsBeforeLongBreak: Int = 4,
) {}