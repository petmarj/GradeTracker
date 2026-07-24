package com.example.gradetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gradetracker.data.API.NetworkClient
import com.example.gradetracker.data.API.TokenStore
import com.example.gradetracker.data.SchedulerViewModelFactory
import com.example.gradetracker.repo.SchedulerRepository
import com.example.gradetracker.ui.theme.GradeTrackerTheme
import com.example.gradetracker.ui.theme.components.GradeTrackerNavigationBar
import com.example.gradetracker.ui.theme.components.SchedulerViewModel
import com.example.gradetracker.ui.theme.components.TopLevelDestination
import com.example.gradetracker.ui.theme.screens.HomeScreen
import com.example.gradetracker.ui.theme.screens.PlaceholderScreen
import com.example.gradetracker.ui.theme.screens.ScheduleScreen
import com.example.gradetracker.ui.theme.screens.SchoolYearScreen
import com.example.gradetracker.ui.theme.screens.SubjectScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GradeTrackerTheme {
                GradeTrackerApp()
            }
        }
    }
}

@Composable
private fun GradeTrackerApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route


    Scaffold(
        bottomBar = {
                GradeTrackerNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )

        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.GRADES.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TopLevelDestination.GRADES.route) {
                HomeScreen(navController)
            }
            composable(TopLevelDestination.SCHEDULE.route) {
                val tokenStore = remember {
                    object : TokenStore {}
                }

                val repository = remember {
                    SchedulerRepository(
                        api = NetworkClient.lerbermattApi,
                        tokenStore = tokenStore
                    )
                }

                val factory = remember {
                    SchedulerViewModelFactory(repository)
                }

                val schedulerViewModel: SchedulerViewModel = viewModel(
                    factory = factory
                )

                ScheduleScreen(
                    viewModel = schedulerViewModel
                )

            }
            composable(TopLevelDestination.STATS.route) {
                PlaceholderScreen(title = TopLevelDestination.STATS.label)
            }
            composable(TopLevelDestination.SETTINGS.route) {
                PlaceholderScreen(title = TopLevelDestination.SETTINGS.label)
            }
            composable(
                route = "schoolYearScreen/{schoolYearId}",
                arguments = listOf(navArgument("schoolYearId") {
                    type = NavType.StringType
                })
            ) { entry ->
                SchoolYearScreen(
                    navController = navController,
                    schoolYearId = entry.arguments?.getString("schoolYearId")
                )
            }
            composable(
                route = "subjectScreen/{subjectId}",
                arguments = listOf(navArgument("subjectId") {
                    type = NavType.StringType
                })
            ) { entry ->
                SubjectScreen(
                    navController = navController,
                    subjectId = entry.arguments?.getString("subjectId")
                )
            }
        }
    }
}
