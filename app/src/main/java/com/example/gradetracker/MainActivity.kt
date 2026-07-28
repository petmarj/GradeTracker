package com.example.gradetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.gradetracker.data.preferences.AppPreferences
import com.example.gradetracker.data.preferences.AppSettings
import com.example.gradetracker.data.remote.SharedPreferencesTokenStore
import com.example.gradetracker.data.repository.StudentRepository
import com.example.gradetracker.model.GradeSort
import com.example.gradetracker.model.SubjectSort
import com.example.gradetracker.ui.navigation.MoreRoutes
import com.example.gradetracker.ui.settings.SettingsScreen
import com.example.gradetracker.ui.settings.SettingsViewModel
import com.example.gradetracker.ui.settings.SettingsViewModelFactory
import com.example.gradetracker.ui.student.StudentScreen
import com.example.gradetracker.ui.student.StudentViewModel
import com.example.gradetracker.ui.student.StudentViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            val appPreferences = remember(context) {
                AppPreferences(context)
            }

            val appSettings by appPreferences.settings.collectAsState()

            GradeTrackerTheme(
                themeMode = appSettings.themeMode,
                dynamicColor = appSettings.dynamicColors
            ) {
                GradeTrackerApp(
                    appSettings = appSettings,
                    appPreferences = appPreferences
                )
            }
        }
    }
}

@Composable
private fun GradeTrackerApp(
    appSettings: AppSettings,
    appPreferences: AppPreferences
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val subjectSortBySchoolYear = remember {
        mutableStateMapOf<String, SubjectSort>()
    }

    val gradeSortBySubject = remember {
        mutableStateMapOf<String, GradeSort>()
    }
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
                HomeScreen(
                    navController = navController,
                    gradeColorMode = appSettings.gradeColorMode
                )
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
            composable(MoreRoutes.SETTINGS) {
                val context = LocalContext.current

                val tokenStore: TokenStore = remember(context) {
                    SharedPreferencesTokenStore(context)
                }
                val schedulerRepository = remember {
                    SchedulerRepository(
                        api = NetworkClient.lerbermattApi,
                        tokenStore = tokenStore
                    )
                }
                val factory = remember {
                    SettingsViewModelFactory(
                        tokenStore,
                        schedulerRepository,
                        appPreferences
                    )
                }
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = factory
                )

                SettingsScreen(
                    viewModel = settingsViewModel,
                    themeMode = appSettings.themeMode,
                    onThemeModeChange = appPreferences::setThemeMode
                )
            }
            composable(
                route = "schoolYearScreen/{schoolYearId}",
                arguments = listOf(navArgument("schoolYearId") {
                    type = NavType.StringType
                })
            ) { entry ->
                val schoolYearId = requireNotNull(
                    entry.arguments?.getString("schoolYearId")
                )
                val currentSort =
                    subjectSortBySchoolYear[schoolYearId]
                        ?: appSettings.subjectSort
                SchoolYearScreen(
                    navController = navController,
                    schoolYearId = entry.arguments?.getString("schoolYearId"),
                    defaultSubjectSort = appSettings.subjectSort,
                    gradeColorMode = appSettings.gradeColorMode,
                    subjectSort = currentSort,
                    onSubjectSortChange = { newSort ->
                        subjectSortBySchoolYear[schoolYearId] = newSort
                    },

                )
            }
            composable(
                route = "subjectScreen/{subjectId}",
                arguments = listOf(navArgument("subjectId") {
                    type = NavType.StringType
                })
            ) { entry ->
                val subjectId = requireNotNull(
                    entry.arguments?.getString("subjectId")
                )

                val currentSort =
                    gradeSortBySubject[subjectId]
                        ?: appSettings.gradeSort
                SubjectScreen(
                    navController = navController,
                    subjectId = entry.arguments?.getString("subjectId"),
                    defaultGradeSort = appSettings.gradeSort,
                    gradeColorMode = appSettings.gradeColorMode,
                    gradeSort = currentSort,
                    onGradeSortChange = { newSort ->
                        gradeSortBySubject[subjectId] = newSort
                    }
                )
            }
            composable(MoreRoutes.STUDENT) {
                val context = LocalContext.current

                val tokenStore: TokenStore = remember(context) {
                    SharedPreferencesTokenStore(context)
                }
                val studentRepository = remember {
                    StudentRepository(
                        api = NetworkClient.lerbermattApi,
                        tokenStore = tokenStore
                    )
                }
                val factory = remember {
                    StudentViewModelFactory(
                        studentRepository
                    )
                }
                val studentViewModel: StudentViewModel = viewModel(
                    factory = factory
                )
                StudentScreen(
                    studentViewModel
                )
            }

            composable(MoreRoutes.HELP) {
                PlaceholderScreen("Hilfe")
            }
            composable(MoreRoutes.ABSENCES) {
                PlaceholderScreen("Absenzen")
            }
            composable(MoreRoutes.MENSA) {
                PlaceholderScreen("Mensa")
            }
        }
    }
}
