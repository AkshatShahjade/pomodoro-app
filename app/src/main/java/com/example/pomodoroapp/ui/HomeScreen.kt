package com.example.pomodoroapp.ui

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material.icons.twotone.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoroapp.R
import com.example.pomodoroapp.ui.theme.PomodoroAppTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration

private const val TAG = "HOMESCREEN"

@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               pomodoroViewModel: PomodoroViewModel = viewModel(),
               pomodoroUiState: PomodoroUiState,
               onSettingsClick: ()->Unit={},
               onInfoClick: ()->Unit={},
               onBackupClick: ()->Unit={},
               onStatisticsClick: ()->Unit={},
   ){

    var menuVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1.1f))

        HomePageTimer(modifier = Modifier,
            timer = pomodoroUiState.timer,
            onTimerClick = pomodoroViewModel::toggleTimer,
            onTimerSwipeDown = pomodoroViewModel::startNextTimer,
        )

        Spacer(Modifier.weight(1f))

        Box(modifier = Modifier
            .align(Alignment.Start)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            if (menuVisible) {
                Menu(modifier = Modifier,
                    statisticsLambda = onStatisticsClick,
                    infoLambda = onInfoClick,
                    settingsLambda = onSettingsClick,
                    backupLambda = onBackupClick,
                )
            }
        }
        BottomHomePageRow({menuVisible = !menuVisible})
        if (pomodoroUiState.isTimerEnded) {
            TimerDialog(
                onStartNext = pomodoroViewModel::startNextTimer,
                onExtend1min = pomodoroViewModel::extendTimer1min
            )
        }
    }
}

@Composable
fun HomePageTimer(modifier: Modifier,
                  timer: String,
                  onTimerClick: () -> Unit,
                  onTimerSwipeDown: () -> Unit
                  ) {
    TextButton(
        onClick = { onTimerClick() },
        modifier = modifier.pointerInput(Unit) {
            var totalDrag = 0f
            detectVerticalDragGestures(
                onDragEnd = {
                    if(totalDrag>0f) {
                        onTimerSwipeDown()
                    }
                    totalDrag = 0f
                },
                onDragStart = {},
                onDragCancel = {},
                onVerticalDrag = { _, dragAmount ->
                    totalDrag = dragAmount
                }
            )
        },
    ) {
        Text(
            text = "%01d:%02d:%02d".format(
                Duration.parse(timer).inWholeHours % 60,
                Duration.parse(timer).inWholeMinutes % 60,
                Duration.parse(timer).inWholeSeconds % 60
            ),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TimerDialog(
    onExtend1min: () -> Unit = {},
    onStartNext: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            onStartNext()
        },
        title = { Text(text = "Time's Up") }, // Work phase Up, Rest Phase up, etc.
//        text = { Text(text = "123") },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = onExtend1min
            ) {
                Text(text = "Extend by 1min")
            }
        },
        confirmButton = {
            TextButton(onClick = onStartNext) {
                Text(text = "Reset")
            }
        }
    )
}

//@Preview
//@Composable
//fun DialogPreview(){
//    PomodoroAppTheme(darkTheme = true) {
//        TimerDialog()
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewLight(){
//
//    PomodoroAppTheme(darkTheme = false) {
//        Surface(
//            modifier = Modifier.fillMaxSize()
//        ){
////            HomePage(startTime = "25m")
//            HomeScreen()
////            Menu()
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun PreviewDark(){

    PomodoroAppTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ){
//            HomeScreen()
//            HomePage(startTime = "25m")
//            Menu()
        }
    }
}

@Composable
fun MenuItem(isClickable: Boolean = true, pic: ImageVector, title: String, onClickLambda: ()->Unit = {}, modifier: Modifier = Modifier){
    if(isClickable) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .clickable { onClickLambda() }
        ) {
            Icon(
                pic, contentDescription = "", // To Add
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .padding(start = dimensionResource(R.dimen.padding_small))
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
    } else{
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = modifier
//                .padding(dimensionResource(R.dimen.padding_small))
//                .padding(horizontal = dimensionResource(R.dimen.padding_small))
//                .background(color = MaterialTheme.colorScheme.primary,
//                    shape = MaterialTheme.shapes.medium)
//        ) {
////            Icon(
////                pic, contentDescription = "Menu",
////                tint = MaterialTheme.colorScheme.onPrimary,
////                modifier = Modifier
////                    .padding(dimensionResource(R.dimen.padding_small))
////                    .padding(start = dimensionResource(R.dimen.padding_small))
////            )
//            Text(
//                text = title,
//                style = MaterialTheme.typography.titleLarge,
//                color = MaterialTheme.colorScheme.onPrimary,
//                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
//
//            )
//        }
    }
}

@Composable
fun Menu(modifier: Modifier = Modifier,
         statisticsLambda: ()->Unit,
         backupLambda: ()->Unit,
         settingsLambda: ()->Unit,
         infoLambda: ()->Unit,
     ){
    Column(
        modifier = modifier
    ){
        MenuItem(isClickable = false, Icons.TwoTone.FavoriteBorder, stringResource(R.string.pomodoro_timer),)
        MenuItem(pic = Icons.Outlined.Star, title = stringResource(R.string.statistics),
            onClickLambda = statisticsLambda)
        MenuItem(pic = Icons.Outlined.Refresh, title =  stringResource(R.string.back_up),
            onClickLambda = backupLambda)
        MenuItem(pic = Icons.Sharp.Settings, title =  stringResource(R.string.settings),
            onClickLambda = settingsLambda)
        MenuItem(pic = Icons.Outlined.Info, title =  stringResource(R.string.about),
            onClickLambda = infoLambda)
    }
}



@Composable
fun BottomHomePageRow(onClickMenu: ()->Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onClickMenu,
            modifier = Modifier
        ) {
            Icon(
                Icons.Filled.Menu, contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }

        Spacer(modifier.weight(1f))
        IconButton(
            onClick = {},
            modifier = Modifier
        ) {
            Icon(
                Icons.Filled.Add, contentDescription = "Exclamation",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
        IconButton(
            onClick = {},
            modifier = Modifier
        ) {
            Icon(
                Icons.Filled.ThumbUp, contentDescription = "Streak",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
        }
        Box(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .background(
                    MaterialTheme.colorScheme.primaryContainer, shape = CircleShape
                )

        ) {
            Icon(
                Icons.Filled.Face, contentDescription = "Steak is 1",
                tint = Color.Transparent,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
            Text(
                text = "1",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.Center),
            )
        }

    }
}