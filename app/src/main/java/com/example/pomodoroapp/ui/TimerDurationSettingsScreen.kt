package com.example.pomodoroapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.pomodoroapp.R
import com.example.pomodoroapp.data.DataSource
import com.example.pomodoroapp.ui.theme.PomodoroAppTheme
import kotlin.math.roundToInt

@Composable
fun TimerDurationSettingsScreen(
    modifier: Modifier = Modifier,
    pomodoroViewModel: PomodoroViewModel,
    pomodoroUiState: PomodoroUiState
){
//    var currentTimerProfile by rememberSaveable { mutableStateOf(DataSource.timerProfileList[0]) }

    val currentTimerProfile = pomodoroUiState.timerProfile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
    ) {
        SettingMenuItem(
            modifier = Modifier,
            title = "Profile",
            value = currentTimerProfile.name,
            onClick = {}
        )

        SliderSettingItem(
            modifier = Modifier,
            title = stringResource(R.string.work_session_duration),
            valueRange = DataSource.workDurationLimits,
            value = currentTimerProfile.workDuration,
            onSliderValueChange = pomodoroViewModel::updateWorkDuration
        )
        SliderSettingItem(
            modifier = Modifier,
            title = stringResource(R.string.break_session_duration),
            valueRange = DataSource.breakDurationLimits,
            value = currentTimerProfile.breakDuration,
            onSliderValueChange = pomodoroViewModel::updateBreakDuration
        )

        SettingMenuItem(
            modifier = Modifier,
            title = "Enable Long Breaks",
            isSwitch = true,
            value = currentTimerProfile.longBreaksOn,
            onClick = pomodoroViewModel::updateLongBreaksOn
        )

        if(currentTimerProfile.longBreaksOn){
            SliderSettingItem(
                modifier = Modifier,
                title = "Long Break Duration",
                valueRange = DataSource.breakDurationLimits,
                value = currentTimerProfile.longBreakDuration,
                onSliderValueChange = pomodoroViewModel::updateLongBreakDuration
            )
            SliderSettingItem(
                modifier = Modifier,
                title = "Sessions before a long break",
                valueRange = DataSource.sessionsBeforeLongBreakLimits,
                value = currentTimerProfile.sessionsBeforeLongBreak,
                onSliderValueChange = pomodoroViewModel::updateSessionsBeforeLongBreak
            )
        }
    }
}



@Composable
fun SliderSettingItem(
    modifier: Modifier = Modifier,
    title:String,
    valueRange: ClosedFloatingPointRange<Float>,
    value: Int,
    onSliderValueChange: (Float)->Unit,
){
    val steps = (valueRange.endInclusive-valueRange.start - 1).roundToInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .weight(1f),
                value = value.toFloat(),
                onValueChange =  onSliderValueChange,
                valueRange = valueRange,
                steps = steps,
            )
            Text(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerDurationSettingsScreenPreview(){

    PomodoroAppTheme (darkTheme = false) {
//        sliderSettingItem()
    }
}