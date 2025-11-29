package com.taskwheel.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taskwheel.app.data.WheelRepository
import com.taskwheel.app.ui.MainScreen
import com.taskwheel.app.ui.SettingsScreen
import com.taskwheel.app.ui.WheelViewModel
import com.taskwheel.app.ui.theme.TaskWheelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = WheelRepository(applicationContext)

        setContent {
            TaskWheelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: WheelViewModel = viewModel(
                        factory = WheelViewModel.Factory(repository)
                    )

                    TaskWheelApp(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Settings : Screen("settings")
}

@Composable
fun TaskWheelApp(
    navController: NavHostController,
    viewModel: WheelViewModel
) {
    val segments by viewModel.segments.collectAsState()
    val isSpinning by viewModel.isSpinning.collectAsState()
    val selectedSegment by viewModel.selectedSegment.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                segments = segments,
                isSpinning = isSpinning,
                selectedSegment = selectedSegment,
                onSpinClick = { viewModel.startSpin() },
                onSpinComplete = { segment -> viewModel.stopSpin(segment) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onDismissResult = { viewModel.clearSelection() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                segments = segments,
                onBackClick = { navController.popBackStack() },
                onSaveSegments = { updatedSegments ->
                    viewModel.saveSegments(updatedSegments)
                },
                onResetToDefaults = { viewModel.resetToDefaults() }
            )
        }
    }
}
