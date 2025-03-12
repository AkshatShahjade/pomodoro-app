package com.example.pomodoroapp.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PomodoroViewModel: ViewModel() {
    private val _uiState: MutableStateFlow<PomodoroUiState> = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()
    val startTimer = "2s"

    init{
        _uiState.value = PomodoroUiState(timer = startTimer) // change to timer settings value
    }

    fun toggleTimer(){
        _uiState.update { it.copy(isTimerRunning = !it.isTimerRunning) }
    }

    fun timerEnd(){
        _uiState.update { it.copy(
            isTimerRunning = false,
            isTimerEnded = true,
        ) }
    }

    fun startNextTimer(){
        _uiState.update { it.copy(
            timer = startTimer,
            isTimerEnded = false,
            isTimerRunning = false
        ) }
    }

    fun extendTimer1min(){
        _uiState.update { it.copy(
            timer = (Duration.parse(it.timer)+1.minutes).toString(),
            isTimerEnded = false,
            isTimerRunning = true
        ) }
    }

    fun decreaseTimer(sec:Int){
        _uiState.update { it.copy (
            timer = (Duration.parse(it.timer) - sec.seconds).toString()
        ) }
    }

}