package com.example.gradetracker

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.content.edit
import com.example.gradetracker.data.preferences.SortPreferences
import com.example.gradetracker.data.remote.SharedPreferencesTokenStore
import com.example.gradetracker.ui.settings.SettingsScreen
import com.example.gradetracker.ui.settings.SettingsViewModel
import com.example.gradetracker.ui.settings.SettingsViewModelFactory
import com.example.gradetracker.ui.theme.ThemeMode
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current

            val preferences = remember(context) {
                context.getSharedPreferences(
                    "app_settings",
                    Context.MODE_PRIVATE
                )
            }
            val sortPreferences = remember(context) {
                SortPreferences(context)
            }

            var themeMode by remember {
                mutableStateOf(
                    runCatching {
                        ThemeMode.valueOf(
                            preferences.getString(
                                "theme_mode",
                                ThemeMode.SYSTEM.name
                            )!!
                        )
                    }.getOrDefault(ThemeMode.SYSTEM)
                )
            }

            GradeTrackerTheme(
                themeMode = themeMode
            ) {
                GradeTrackerApp(
                    themeMode = themeMode,
                    onThemeModeChange = { newMode ->
                        themeMode = newMode

                        preferences.edit {
                            putString("theme_mode", newMode.name)
                        }


                    },
                    sortPreferences = sortPreferences
                )
            }
        }
    }
}

@Composable
private fun GradeTrackerApp(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    sortPreferences: SortPreferences
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val defaultSubjectSort by
    sortPreferences.subjectSort.collectAsState()

    val defaultGradeSort by
    sortPreferences.gradeSort.collectAsState()


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
                        sortPreferences
                    )
                }
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = factory
                )

                SettingsScreen(
                    viewModel = settingsViewModel,
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange
                )
            }
            composable(
                route = "schoolYearScreen/{schoolYearId}",
                arguments = listOf(navArgument("schoolYearId") {
                    type = NavType.StringType
                })
            ) { entry ->
                SchoolYearScreen(
                    navController = navController,
                    schoolYearId = entry.arguments?.getString("schoolYearId"),
                    defaultSubjectSort = defaultSubjectSort
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
                    subjectId = entry.arguments?.getString("subjectId"),
                    defaultGradeSort = defaultGradeSort
                )
            }
        }
    }
}
