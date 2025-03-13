package com.example.pomodoroapp.ui

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.pomodoroapp.R
import com.example.pomodoroapp.data.DataSource
import com.example.pomodoroapp.data.Notification
import com.example.pomodoroapp.ui.theme.PomodoroAppTheme

@Composable
fun SettingsScreen(modifier: Modifier = Modifier,
                   onTimerDurationSettingsClick: ()->Unit = {},
                   pomodoroUiState: PomodoroUiState,
                   pomodoroViewModel: PomodoroViewModel,){

        // TODO: The adaptive arrangement of the row elements aren't ideal, currently using weight for them, but will need to change...
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            item{
                SettingTitle(modifier = Modifier,
                    textId = R.string.general,)
            }
            item {
                SettingMenuItem(
                    modifier = Modifier,
                    imageVector = Icons.Filled.Settings,
                    title = stringResource(R.string.timer_duration_settings),
                    description = stringResource(R.string.set_work_break_session_durations),
                    onClick = onTimerDurationSettingsClick,
                )
            }
            item {
                SettingMenuItem(
                    modifier = Modifier,
                    title = stringResource(R.string.keep_the_screen_on),
                    isSwitch = false,
                    value = pomodoroUiState.keepScreenOn,
                    onClick = pomodoroViewModel::toggleKeepScreenOn,
                )
            }
            item {
                SettingMenuItem(
                    modifier = Modifier,
                    title = stringResource(R.string.fullscreen_mode),
                    isSwitch = false,
                    value = pomodoroUiState.fullScreenOn,
                    onClick = pomodoroViewModel::toggleFullScreenOn,
                )
            }
            item {
                SettingMenuItem(
                    modifier = Modifier,
                    title = stringResource(R.string.dark_theme),
                    isSwitch = true,
                    value = pomodoroUiState.inDarkMode,
                    onClick = pomodoroViewModel::toggleDarkMode,
                )
            }
            item{
                SettingTitle(modifier = Modifier,
                    textId = R.string.notifications,)
            }
            item {
                val soundOn = pomodoroUiState.notificationSoundOn
                Column(
                    modifier = Modifier.animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium,
                        )
                    ),
                ) {
                    SettingMenuItem(
                        modifier = Modifier,
                        title = stringResource(R.string.sound_enabled),
                        isSwitch = true,
                        value = soundOn,
                        onClick = pomodoroViewModel::toggleNotificationSoundOn
                    )
                    //Dialog Box to Choose
                    if (soundOn) {
                        var dialogVisible by rememberSaveable { mutableStateOf(false) }
                        SettingMenuItem(
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_medium)),
                            //                    title = stringResource(R.string.finished_work_notification_sound),
                            title = stringResource(R.string.notification_sound),
                            description = stringResource(R.string.set_notification_sound),
                            onClick = {dialogVisible = !dialogVisible}
                        )
                        if(dialogVisible) {
                            Dialog(
                                onDismissRequest = {dialogVisible = false}
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = MaterialTheme.shapes.large
                                        )
                                        .padding(dimensionResource(R.dimen.padding_medium))
                                ) {
                                    Text(
                                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                                        text = stringResource(R.string.notification_sound),
                                        style = MaterialTheme.typography.titleLarge
                                    )

                                    var selectedNotification: Notification by rememberSaveable{
                                        mutableStateOf(pomodoroUiState.notificationSound) }

                                    LazyColumn(
                                        modifier = Modifier.heightIn(max = 250.dp)
                                    ) {
                                        items(DataSource.notificationSoundsList) { notification ->
                                            NotificationDialogItem(
                                                modifier = Modifier.background(
                                                    color = if(notification == selectedNotification && pomodoroUiState.inDarkMode)
                                                        MaterialTheme.colorScheme.surfaceBright
                                                    else if (notification == selectedNotification && !pomodoroUiState.inDarkMode)
                                                        MaterialTheme.colorScheme.surfaceDim
                                                    else MaterialTheme.colorScheme.surface
                                                ),
                                                notification = notification,
                                                context = LocalContext.current,
                                                onClick = {
                                                    selectedNotification = notification
                                                }
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ){
                                        TextButton(
                                            modifier = Modifier.padding(horizontal =  dimensionResource(R.dimen.padding_small)),
                                            onClick = {dialogVisible = false}
                                        ){
                                            Text(
                                                text = "CANCEL"
                                            )
                                        }
                                        TextButton(
                                            modifier = Modifier.padding(horizontal =  dimensionResource(R.dimen.padding_small)),
                                            onClick = {
                                                pomodoroViewModel.updateNotificationSound(selectedNotification)
                                                dialogVisible = false
                                            }
                                        ){
                                            Text(
                                                text = "OK"
                                            )
                                        }
                                    }
                                }
                            }

                        }
                        //                    item{
                        //                        SettingMenuItem(
                        //                            modifier = Modifier,
                        //                            title = stringResource(R.string.finished_break_notification_sound),
                        //                            description = stringResource(R.string.set_notification_sound)
                        //                        )
                        //                    }
                    }
                }
            }
            item {
                SettingMenuItem(
                    modifier = Modifier,
                    title = stringResource(R.string.vibration_enabled),
                    isSwitch = true,
                    value = pomodoroUiState.notificationVibrationOn,
                    onClick = pomodoroViewModel::toggleNotificationVibrationOn
                )
            }
            item {
                Column(
                    modifier = Modifier.animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium,
                        )
                    ),
                ) {
                    val insistentNotificationOn = pomodoroUiState.insistentNotificationOn
                    SettingMenuItem(
                        modifier = Modifier,
                        title = stringResource(R.string.insistent_notifications),
                        description = stringResource(R.string.repeat_the_notifications_until_cancelled),
                        isSwitch = false,
                        value = insistentNotificationOn,
                        onClick = pomodoroViewModel::toggleInsistentNotificationOn,
                    )
                    if (!insistentNotificationOn) {
                        SettingMenuItem(
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_medium)),
                            title = stringResource(R.string.autostart_work),
                            isSwitch = false,
                            value = pomodoroUiState.autoStartWorkOn,
                            onClick = pomodoroViewModel::toggleAutoStartWorkOn,
                        )
                        SettingMenuItem(
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_medium)),
                            title = stringResource(R.string.autostart_break),
                            isSwitch = false,
                            value = pomodoroUiState.autoStartBreakOn,
                            onClick = pomodoroViewModel::toggleAutoStartBreakOn,
                        )
                    }
                }
            }
            item {
                SettingMenuItem(
                    modifier = Modifier,
                    title = stringResource(R.string.pre_notification),
                    description = stringResource(R.string.notify_1_minute_before_work_session_ends),
                    isSwitch = false,
                    value = pomodoroUiState.preNotificationOn,
                    onClick = pomodoroViewModel::togglePreNotificationOn
                )
            }

            item{
                SettingTitle(modifier = Modifier,
                    textId = R.string.during_work_sessions,
                    )
            }
            item {
                SettingMenuItem(
                    modifier = Modifier,
                    title = stringResource(R.string.disable_sound_and_vibration),
                    description = stringResource(R.string.click_to_grant_permission),
                    isSwitch = false,
                    value = false
                )
            }
            item {
                SettingMenuItem(
                    modifier = Modifier,
                    title = stringResource(R.string.do_not_disturb_mode),
                    description = stringResource(R.string.click_to_grant_permission),
                    isSwitch = false,
                    value = false
                )
            }
        }

    }

@Preview(showBackground = true)
@Composable
fun NotPrev(){
    NotificationDialogItem(notification = DataSource.notificationSoundsList[0], context = LocalContext.current)
}

@Composable
fun NotificationDialogItem(modifier: Modifier = Modifier,
                           notification: Notification,
                           context: Context,
                           onClick: () -> Unit ={},
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ){
        IconButton (
            modifier = Modifier,
            onClick = {
                PomodoroViewModel().playNotificationSound(context = context, notification.id)
            }
        ){
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null
            )
        }
        Text(
            text = notification.name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SettingMenuItem(modifier: Modifier = Modifier,
                    imageVector: ImageVector? = null,
                    @DrawableRes drawableRes: Int? = null,
                    title: String,
                    description: String? = null,
                    value:Any?=null,
                    isDisabled: Boolean = false,
                    onClick: ()->Unit = {},
                    isSwitch: Boolean = false,){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        //How to scale images so that height = height of column...?
        if(imageVector!=null){
            Image(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .padding(end = dimensionResource(R.dimen.padding_small))
                    .weight(0.1f),
                imageVector = imageVector,
                contentDescription = null
            )
        }
        if(drawableRes!=null){
            Image(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .padding(end = dimensionResource(R.dimen.padding_small))
                    .weight(0.1f),
                painter = painterResource(drawableRes),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .padding(horizontal = dimensionResource(R.dimen.padding_small))
                .weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            if(description!=null){
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if(value !is Boolean && value != null){
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

//        Spacer(Modifier.weight(1f))

        if(value is Boolean){
            if(isSwitch){
                Switch(
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .weight(0.2f),
                    checked = value,
                    onCheckedChange = null,
                )
            }
            else {
                Checkbox(
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .weight(0.2f),
                    checked = value,
                    onCheckedChange = null,
                )
            }
        }
    }
}

@Composable
fun SettingTitle(modifier: Modifier = Modifier,
                 @StringRes textId: Int,
                 textStyle: TextStyle = MaterialTheme.typography.titleLarge) {
    Column {
        HorizontalDivider(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_small))
        )
        Text(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
            text = stringResource(textId),
            style = textStyle
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview(){
    PomodoroAppTheme (darkTheme = true){
        SettingsScreen(pomodoroViewModel = PomodoroViewModel(), pomodoroUiState = PomodoroUiState())


//        var settingActive by remember{ mutableStateOf(false) }
//        SettingMenuItem(
//            imageVector = Icons.Filled.Email,
////            drawableRes = R.drawable.img_0818_tiger,
//            modifier = Modifier,
//            title = "Setting",
////            value = "ak.sh@gm.com"
////            value = settingActive,
//            isSwitch = true,
//            onClick = { settingActive = !settingActive },
//            description = "Set the Setting"
//        )
    }
}