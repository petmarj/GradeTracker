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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gradetracker.data.remote.NetworkClient
import com.example.gradetracker.data.remote.TokenStore
import com.example.gradetracker.ui.schedule.SchedulerViewModelFactory
import com.example.gradetracker.data.repository.SchedulerRepository
import com.example.gradetracker.ui.theme.GradeTrackerTheme
import com.example.gradetracker.ui.navigation.GradeTrackerNavigationBar
import com.example.gradetracker.ui.schedule.SchedulerViewModel
import com.example.gradetracker.ui.navigation.TopLevelDestination
import com.example.gradetracker.ui.home.HomeScreen
import com.example.gradetracker.ui.PlaceholderScreen
import com.example.gradetracker.ui.schedule.ScheduleScreen
import com.example.gradetracker.ui.schoolyear.SchoolYearScreen
import com.example.gradetracker.ui.subject.SubjectScreen
import androidx.compose.ui.platform.LocalContext
import com.example.gradetracker.data.remote.SharedPreferencesTokenStore
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
                val context = LocalContext.current

                val tokenStore: TokenStore = remember(context) {
                    SharedPreferencesTokenStore(context)
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
