package com.example.pomodoroapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import com.example.pomodoroapp.data.DataSource

data class PomodoroUiState(
    val timer: String = "25m",
    val isTimerRunning: Boolean = false,
    val isTimerEnded: Boolean = false,
    val timerProfile: TimerProfile = DataSource.timerProfileList[0],
    val timerStage: Int = 0,

    val inDarkMode: Boolean = true,
)

