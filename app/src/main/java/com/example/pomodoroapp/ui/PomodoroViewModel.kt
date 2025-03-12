package com.example.pomodoroapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PomodoroViewModel: ViewModel() {
    private val _uiState: MutableStateFlow<PomodoroUiState> = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()
//    val startTimer = "2s"

    init{
        // TODO: Change this to the saved profile value...
        _uiState.value = PomodoroUiState(
            timer = _uiState.value.timerProfile.workDuration.minutes.toString()
        )
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
        val _timerStage = _uiState.value.timerStage+1
        _uiState.update { it.copy(
            timerStage = _timerStage,
            timer = decideSessionType(_timerStage),
            isTimerEnded = false,
            isTimerRunning = false
        ) }
    }
    private fun decideSessionType(stage: Int): String{
        val currTimerProfile = _uiState.value.timerProfile
        if(stage%2==0) {
            return currTimerProfile.workDuration.minutes.toString()
        }else if( currTimerProfile.longBreaksOn==true
            && (stage%(currTimerProfile.sessionsBeforeLongBreak*2)
            == currTimerProfile.sessionsBeforeLongBreak*2-1)){
            return currTimerProfile.longBreakDuration.minutes.toString()
        }else{
            return currTimerProfile.breakDuration.minutes.toString()
        }
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
    fun updateWorkDuration(newVal: Float){
        _uiState.update { it.copy(
            timerProfile = _uiState.value.timerProfile.copy(
                workDuration = newVal.roundToInt()
            )
        ) }
    }
    fun updateBreakDuration(newVal: Float){
        _uiState.update { it.copy(
            timerProfile = _uiState.value.timerProfile.copy(
                breakDuration = newVal.roundToInt()
            )
        ) }
    }
    fun updateLongBreaksOn(){
        _uiState.update { it.copy(
            timerProfile = _uiState.value.timerProfile.copy(
                longBreaksOn = !_uiState.value.timerProfile.longBreaksOn
            )
        ) }
    }
    fun updateLongBreakDuration(newVal: Float){
        _uiState.update { it.copy(
            timerProfile = _uiState.value.timerProfile.copy(
                longBreakDuration = newVal.roundToInt()
            )
        ) }
    }
    fun updateSessionsBeforeLongBreak(newVal: Float){
        _uiState.update { it.copy(
            timerProfile = _uiState.value.timerProfile.copy(
                sessionsBeforeLongBreak = newVal.toInt(),
            ),
            timerStage = 0,
        ) }
    }

    var counter = 0
    fun setColorModeOnce(inDarkMode: Boolean){
        if(counter==0) {
            _uiState.update {
                it.copy(
                    inDarkMode = inDarkMode
                )
            }
            counter++
        }
    }
    fun toggleDarkMode(){
        _uiState.update{ it.copy(
            inDarkMode = !_uiState.value.inDarkMode
        )}
    }
    fun toggleKeepScreenOn(){
        _uiState.update{ it.copy(
            keepScreenOn = !_uiState.value.keepScreenOn
        )}
    }
}