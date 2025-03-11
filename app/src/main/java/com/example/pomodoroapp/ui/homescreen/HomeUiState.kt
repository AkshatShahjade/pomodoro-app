package com.example.pomodoroapp.ui.homescreen

data class HomeUiState(
    val timer: String = "25m",
    val isTimerRunning: Boolean = false,
    val isTimerEnded: Boolean = false,
)