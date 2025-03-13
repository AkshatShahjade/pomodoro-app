package com.example.pomodoroapp.ui

import com.example.pomodoroapp.data.DataSource
import com.example.pomodoroapp.data.Notification
import com.example.pomodoroapp.data.TimerProfile

data class PomodoroUiState(
    val timer: String = "25m",
    val isTimerRunning: Boolean = false,
    val isTimerEnded: Boolean = false,
    val timerProfile: TimerProfile = DataSource.timerProfileList[0],
    val timerStage: Int = 0,

    //TODO: Store these settings in memory so that the user doesn't have to re enter the settings each time
    val inDarkMode: Boolean = true,
    // initial Default values of below to be assigned
    val keepScreenOn: Boolean = false,

    val notificationSoundOn: Boolean = true,
    val notificationSound: Notification = DataSource.notificationSoundsList[0],
    val notificationVibrationOn: Boolean = true, // TODO: Maybe I can set it as enum with different settings like strong, light , SOS, etc.
    val notificationFlashOn: Boolean = true,

    val insistentNotificationOn: Boolean = false,
    val autoStartWorkOn: Boolean = false,
    val autoStartBreakOn: Boolean = false,

    val preNotificationOn: Boolean = false,
    val fullScreenOn: Boolean = false,
)

