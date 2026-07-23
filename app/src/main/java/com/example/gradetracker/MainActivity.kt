package com.example.gradetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gradetracker.ui.theme.GradeTrackerTheme
import com.example.gradetracker.ui.theme.screens.HomeScreen
import com.example.gradetracker.ui.theme.screens.SchoolYearScreen
import androidx.navigation.navArgument
import com.example.gradetracker.ui.theme.components.SchedulerViewModel
import com.example.gradetracker.ui.theme.screens.ApiTestScreen
import com.example.gradetracker.ui.theme.screens.SubjectScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        android.util.Log.d("GradeTracker", "onCreate wurde aufgerufen")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GradeTrackerTheme() {
                ApiTestScreen()
                /*val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "homeScreen"
                ) {
                    composable("homeScreen") {
                        HomeScreen(navController)
                    }
                    composable(
                        route = "schoolYearScreen/{schoolYearId}",
                        arguments = listOf(navArgument("schoolYearId") {
                            type = NavType.StringType
                        }))
                    {
                        backStackEntry ->
                        val id = backStackEntry.arguments?.getString("schoolYearId")
                        SchoolYearScreen(navController, id)
                    }
                    composable(
                        route = "subjectScreen/{subjectId}",
                        arguments = listOf(navArgument("subjectId") {
                            type = NavType.StringType
                        }))
                    {
                            backStackEntry ->
                        val id = backStackEntry.arguments?.getString("subjectId")
                        SubjectScreen(navController, id,)
                    }

                }*/


            }
        }
    }
}
