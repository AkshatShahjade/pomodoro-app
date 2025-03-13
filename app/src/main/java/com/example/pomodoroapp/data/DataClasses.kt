package com.example.pomodoroapp.data

import android.os.Parcelable
import androidx.annotation.RawRes
import kotlinx.android.parcel.Parcelize

data class TimerProfile(
    val name: String = "25/5",
    val workDuration: Int = 25,
    val breakDuration: Int = 5,
    val longBreaksOn: Boolean = false,
    val longBreakDuration: Int = 15,
    val sessionsBeforeLongBreak: Int = 4,
) {}

@Parcelize
data class Notification(
    val name: String,
    @RawRes val id: Int,
): Parcelable

enum class TimerSessionType() {
    WORK,
    BREAK,
    LONGBREAK,
}
