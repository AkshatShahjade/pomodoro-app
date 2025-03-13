package com.example.pomodoroapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pomodoroapp.ui.HomeScreen
import com.example.pomodoroapp.ui.PomodoroViewModel
import com.example.pomodoroapp.ui.SettingsScreen
import com.example.pomodoroapp.ui.TimerDurationSettingsScreen
import com.example.pomodoroapp.ui.theme.PomodoroAppTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration

enum class AppScreens(@StringRes val title: Int){
    HomeScreen(R.string.home_screen),
    SettingsScreen(R.string.settings),
    TimerDurationSettingsScreen(R.string.timer_duration_settings),
}

private const val TAG = "MAIN_ACTIVTIY"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context: Context = this
            val navController: NavHostController = rememberNavController()

            val currentRoute by navController.currentBackStackEntryAsState()
            val currentScreen = AppScreens.valueOf(
                currentRoute?.destination?.route?:AppScreens.HomeScreen.name
            )

            val pomodoroViewModel: PomodoroViewModel = viewModel()
            val pomodoroUiState by pomodoroViewModel.uiState.collectAsState()

            // Screen Settings:
            pomodoroViewModel.setColorModeOnce(isSystemInDarkTheme()) // Set Color Mode to system default

            if(pomodoroUiState.keepScreenOn) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

            // Sets the full screen, and recomposes to changes in fullScreenOn state variable
            pomodoroViewModel.setFullScreen(LocalView.current, pomodoroUiState.fullScreenOn)


            // Timer Coroutine
            LaunchedEffect(pomodoroUiState.isTimerRunning, pomodoroUiState.timer) {
                if (Duration.parse(pomodoroUiState.timer).inWholeSeconds > 0L
                        && pomodoroUiState.isTimerRunning == true) {

                    if(Duration.parse(pomodoroUiState.timer).inWholeSeconds == 60L
                        && pomodoroUiState.preNotificationOn){
                            pomodoroViewModel.playPreNotification(context = context)
                    }

                    delay(1000L) // Wait 1 second
                    pomodoroViewModel.decreaseTimer(1)
                    Log.d(TAG, Duration.parse(pomodoroUiState.timer).inWholeSeconds.toString())
                }
                else if(Duration.parse(pomodoroUiState.timer).inWholeSeconds <= 0L
                        && pomodoroUiState.isTimerRunning == true){
                    pomodoroViewModel.timerEnd(context = context)
                    Log.d(TAG, "ended")
                }
            }

            // UI
            PomodoroAppTheme(darkTheme = pomodoroUiState.inDarkMode) {
                Scaffold (
                    modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
                    topBar = {
                        if( navController.previousBackStackEntry!=null
                            && currentScreen != AppScreens.HomeScreen) {
                            navigationTopBar(
                                currentScreen = currentScreen,
                                onUpCLick = { navController.navigateUp() }
                            )
                        }
                    }
                ){ innerPadding ->

                    NavHost(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        navController = navController,
                        startDestination = AppScreens.HomeScreen.name,
                    ){
                        composable(AppScreens.HomeScreen.name)
                        {
                            HomeScreen(
                                modifier = Modifier,
                                pomodoroViewModel = pomodoroViewModel,
                                pomodoroUiState = pomodoroUiState,
                                onSettingsClick = {navController.navigate(AppScreens.SettingsScreen.name)}
                            )
                        }
                        composable(AppScreens.SettingsScreen.name)
                        {
                            SettingsScreen(
                                modifier = Modifier.padding(innerPadding),
                                onTimerDurationSettingsClick = {
                                    navController.navigate(AppScreens.TimerDurationSettingsScreen.name)
                                },
                                pomodoroViewModel = pomodoroViewModel,
                                pomodoroUiState = pomodoroUiState,
                            )
                        }
                        composable(AppScreens.TimerDurationSettingsScreen.name){
                            TimerDurationSettingsScreen(
                                modifier = Modifier,
                                pomodoroViewModel = pomodoroViewModel,
                                pomodoroUiState = pomodoroUiState
                            )
                        }

                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun navigationTopBar(
    currentScreen: AppScreens,
    onUpCLick: ()->Unit,
){
    TopAppBar(
        modifier = Modifier,
        title = {
            Text(
                text = stringResource(currentScreen.title),
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            IconButton(
                modifier = Modifier,
                onClick = onUpCLick,
            ){
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )}
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
    )
}




