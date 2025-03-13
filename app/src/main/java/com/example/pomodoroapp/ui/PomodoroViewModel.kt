package com.example.pomodoroapp.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RawRes
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.data.Notification
import com.example.pomodoroapp.data.TimerSessionType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PomodoroViewModel: ViewModel() {
    private val _uiState: MutableStateFlow<PomodoroUiState> = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    init{
        // TODO: Change this to the saved profile value...
        _uiState.value = PomodoroUiState(
            timer = _uiState.value.timerProfile.workDuration.minutes.toString()
//            timer = "5s"
        )
    }

    fun setFullScreen(view: View, fullScreenOn: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsetsController = view.windowInsetsController
            if (windowInsetsController != null) {
                if (fullScreenOn) {
                    windowInsetsController.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    windowInsetsController.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    windowInsetsController.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                }
            }
        } else {
            @Suppress("DEPRECATION")
            if (fullScreenOn) {
                view.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                        )
            } else {
                view.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    // Notification Sound Functions
    private var mediaPlayer: MediaPlayer? = null
    fun playNotificationSound(context: Context, @RawRes sound: Int) {
        mediaPlayer = MediaPlayer.create(context, sound)
        mediaPlayer?.start()
    }
    private fun playLoopedNotificationSound(context: Context, @RawRes sound: Int) {
        mediaPlayer = MediaPlayer.create(context, sound)
        mediaPlayer?.isLooping=true
        mediaPlayer?.start()
    }
    private fun stopSound() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // Notification Sound Functions
    private var vibrator: Vibrator? = null
    private fun playLoopedNotificationVibration(context: Context){
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator?.hasVibrator() == true) { // Ensure device has a vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 500, 1000) // Delay, Vibration Duration, Pause
                val effect = VibrationEffect.createWaveform(pattern, 1) // Loop indefinitely (index 1)
                vibrator!!.vibrate(effect)
            } else {
                val pattern = longArrayOf(0, 500, 1000)
                @Suppress("DEPRECATION")
                vibrator!!.vibrate(pattern, 1) // Loop indefinitely for old devices
            }
        } else {
            Log.d("Vibration", "Device does not support vibration")
        }
    }
    private fun playNotificationVibration(context: Context){
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator?.hasVibrator() == true) { // Ensure device has a vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        500,
                        255
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(500)
            }
        }else {
            Log.d("Vibration", "Device does not support vibration")
        }
    }
    private fun stopVibration(){
        vibrator?.cancel()
    }

    //todo: uNDERSTAND...
    private fun playNotification(context: Context){
        if (_uiState.value.notificationSoundOn) {
            if(_uiState.value.insistentNotificationOn) {
                playLoopedNotificationSound(context, _uiState.value.notificationSound.id)
            } else {
                playNotificationSound(context, _uiState.value.notificationSound.id)
            }
        }
        if (_uiState.value.notificationVibrationOn){
            if(_uiState.value.insistentNotificationOn){
                playLoopedNotificationVibration(context)
            } else {
                playNotificationVibration(context)
            }
        }
    }
    fun playPreNotification(context: Context){
        if (_uiState.value.notificationSoundOn) {
            playNotificationSound(context, _uiState.value.notificationSound.id)
        }
        if (_uiState.value.notificationVibrationOn){
            playNotificationVibration(context)
        }
    }


    // Timer Related Functions
    fun toggleTimer(){
        _uiState.update { it.copy(isTimerRunning = !it.isTimerRunning) }
    }
    fun decreaseTimer(sec:Int){
        _uiState.update { it.copy (
            timer = (Duration.parse(it.timer) - sec.seconds).toString()
        ) }
    }
    fun timerEnd(context: Context) {
        playNotification(context)
        if(!_uiState.value.insistentNotificationOn){
            if( _uiState.value.autoStartWorkOn
                && getTimerSessionType(_uiState.value.timerStage)
                in listOf(TimerSessionType.BREAK, TimerSessionType.LONGBREAK) ){

                startNextTimer(context)

            } else if (_uiState.value.autoStartBreakOn
                && getTimerSessionType(_uiState.value.timerStage) == TimerSessionType.WORK) {

                startNextTimer(context)

            } else {
                _uiState.update {
                    it.copy(
                        isTimerRunning = false,
                        isTimerEnded = true,
                    )
                }
            }
        } else {

            _uiState.update {
                it.copy(
                    isTimerRunning = false,
                    isTimerEnded = true,
                )
            }

        }
    }
    fun startNextTimer(context: Context){
        //Lets the Notification play for some time instead of immediately cancelling them
        viewModelScope.launch {
            delay(1500L) // 1 second delay
            stopSound()
            stopVibration()
        }

        val nextTimerStage = _uiState.value.timerStage+1
        _uiState.update { it.copy(
            timerStage = nextTimerStage,
            timer = getSessionDuration(getTimerSessionType(nextTimerStage)),
            isTimerEnded = false,
            isTimerRunning = true
        ) }
        updateDndMode(context)
    }
    fun extendTimer1min(){
        stopSound()
        stopVibration()
        _uiState.update { it.copy(
            timer = (Duration.parse(it.timer)+1.minutes).toString(),
            isTimerEnded = false,
            isTimerRunning = true
        ) }
    }

    private fun getSessionDuration(session: TimerSessionType): String{
        return when(session){
            TimerSessionType.WORK -> _uiState.value.timerProfile.workDuration.minutes.toString()
            TimerSessionType.BREAK -> _uiState.value.timerProfile.breakDuration.minutes.toString()
            else -> _uiState.value.timerProfile.longBreakDuration.minutes.toString()
        }
    }
    private fun getTimerSessionType(stage: Int): TimerSessionType{
        val currTimerProfile = _uiState.value.timerProfile
        if(stage%2==0) {
            return TimerSessionType.WORK
        }else if( currTimerProfile.longBreaksOn==true
            && (stage%(currTimerProfile.sessionsBeforeLongBreak*2)
            == currTimerProfile.sessionsBeforeLongBreak*2-1)){
            return TimerSessionType.LONGBREAK
        }else{
            return TimerSessionType.BREAK
        }
    }

    // Timer Profile Setting Functions
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
    fun updateLongBreakDuration(newVal: Float){
        _uiState.update { it.copy(
            timerProfile = _uiState.value.timerProfile.copy(
                longBreakDuration = newVal.roundToInt()
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
    fun updateSessionsBeforeLongBreak(newVal: Float){
        _uiState.update { it.copy(
            timerProfile = _uiState.value.timerProfile.copy(
                sessionsBeforeLongBreak = newVal.toInt(),
            ),
            timerStage = 0,
        ) }
    }

    // Settings related Functions
    private var counter = 0 //TODO: Make more elegant
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

    fun updateNotificationSound(newNotification: Notification?){
        if(newNotification != null){
            _uiState.update { it.copy(
                notificationSound = newNotification
            ) }
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
    fun toggleInsistentNotificationOn(){
        _uiState.update{ it.copy(
            insistentNotificationOn = !_uiState.value.insistentNotificationOn
        )}
    }
    fun toggleNotificationSoundOn(){
        _uiState.update{ it.copy(
            notificationSoundOn = !_uiState.value.notificationSoundOn
        )}
    }
    fun toggleNotificationVibrationOn() {
        _uiState.update {
            it.copy(
                notificationVibrationOn = !_uiState.value.notificationVibrationOn
            )
        }
    }
    fun toggleNotificationFlashOn() {
        _uiState.update {
            it.copy(
                notificationFlashOn = !_uiState.value.notificationFlashOn
            )
        }
    }
    fun togglePreNotificationOn() {
        _uiState.update {
            it.copy(
                preNotificationOn = !_uiState.value.preNotificationOn
            )
        }
    }
    fun toggleFullScreenOn() {
        _uiState.update {
            it.copy(
                fullScreenOn = !_uiState.value.fullScreenOn
            )
        }
    }
    fun toggleAutoStartWorkOn() {
        _uiState.update {
            it.copy(
                autoStartWorkOn = !_uiState.value.autoStartWorkOn
            )
        }
    }
    fun toggleAutoStartBreakOn() {
        _uiState.update {
            it.copy(
                autoStartBreakOn = !_uiState.value.autoStartBreakOn
            )
        }
    }

    // DND Mode
    fun toggleDndMode() {
        _uiState.update {
            it.copy(
                dndMode = !_uiState.value.dndMode
            )
        }
    }
    fun requestDndPermission(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            context.startActivity(intent) // Opens settings for the user to grant permission
        }
    }
    private fun enableDoNotDisturb(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.isNotificationPolicyAccessGranted) {
            // Set Do Not Disturb mode to "Priority only"
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        } else {
            requestDndPermission(context)
        }
    }
    private fun disableDoNotDisturb(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.isNotificationPolicyAccessGranted) {
            // Set Do Not Disturb mode to "Priority only"
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        } else {
            requestDndPermission(context)
        }
    }
    fun updateDndMode(context: Context){
        val sessionType = getTimerSessionType(_uiState.value.timerStage)
        if(_uiState.value.dndMode
            && sessionType == TimerSessionType.WORK
            && _uiState.value.isTimerRunning){
            enableDoNotDisturb(context)
        } else {
            disableDoNotDisturb(context)
        }
    }


}
