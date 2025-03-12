package com.example.pomodoroapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomodoroapp.ui.HomeScreen
import com.example.pomodoroapp.ui.PomodoroViewModel
import com.example.pomodoroapp.ui.SettingsScreen
import com.example.pomodoroapp.ui.theme.PomodoroAppTheme

enum class AppScreens(@StringRes val title: Int){
    HomeScreen(R.string.home_screen),
    SettingsScreen(R.string.settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PomodoroAppTheme {
                Scaffold (
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        navigationTopBar()
                    }
                ){ innerPadding ->
                    val navController: NavHostController = rememberNavController()

                    val pomodoroViewModel: PomodoroViewModel = viewModel()
                    val pomodoroUiState by pomodoroViewModel.uiState.collectAsState()

                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        startDestination = AppScreens.HomeScreen.name,
                    ){
                        composable(AppScreens.HomeScreen.name)
                        {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                pomodoroViewModel = pomodoroViewModel,
                                pomodoroUiState = pomodoroUiState,
                                onSettingsClick = {navController.navigate(AppScreens.SettingsScreen.name)}
                            )
                        }
                        composable(AppScreens.SettingsScreen.name)
                        {
                            SettingsScreen(
                                modifier = Modifier.padding(innerPadding),
//                                pomodoroViewModel = pomodoroViewModel,
//                                pomodoroUiState = pomodoroUiState
                            )
                        }

                    }

                }
            }
        }
    }
}

@Composable
fun navigationTopBar(){}




