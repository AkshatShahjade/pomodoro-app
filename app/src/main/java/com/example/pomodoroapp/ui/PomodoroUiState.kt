package com.example.pomodoroapp.ui

data class PomodoroUiState(
    val timer: String = "25m",
    val isTimerRunning: Boolean = false,
    val isTimerEnded: Boolean = false,
)