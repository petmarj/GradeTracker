package com.example.gradetracker.ui.navigation

import android.text.Layout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

enum class TopLevelDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    GRADES(
        route = "homeScreen",
        label = "Noten",
        selectedIcon = Icons.Filled.School,
        unselectedIcon = Icons.Outlined.School
    ),
    SCHEDULE(
        route = "scheduleScreen",
        label = "Stundenplan",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    ),
    STATS(
        route = "statsScreen",
        label = "Stats",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    )

}
object MoreRoutes {
    const val SETTINGS = "settingsScreen"
    const val STUDENT = "studentScreen"
    const val HELP = "helpScreen"
    const val ABSENCES = "Absenzen"
    const val MENSA = "Mensa"
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeTrackerNavigationBar(
    navController: NavHostController,
    currentRoute: String?
) {
    var moreMenuExpanded by remember {
        mutableStateOf(false)
    }

    val moreRoutes = setOf(
        MoreRoutes.STUDENT,
        MoreRoutes.HELP,
        MoreRoutes.SETTINGS,
        MoreRoutes.ABSENCES,
        MoreRoutes.MENSA
    )

    fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    Box() {
    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            val selected = when (destination) {
                TopLevelDestination.GRADES -> currentRoute in setOf(
                    "homeScreen",
                    "schoolYearScreen/{schoolYearId}",
                    "subjectScreen/{subjectId}"
                )

                else -> currentRoute == destination.route
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != destination.route) {
                        navigateTo(route = destination.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        },
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) }
            )

        }
        NavigationBarItem(
            modifier = Modifier.fillMaxWidth(),
            selected = currentRoute in moreRoutes,
            onClick = {
                moreMenuExpanded = true
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.MoreHoriz,
                    contentDescription = "Mehr"
                )
            },
            label = {
                Text("Mehr")
            }
        )
    }
        Box(
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            if (moreMenuExpanded) {
                ModalBottomSheet(
                    onDismissRequest = {
                        moreMenuExpanded = false
                    }
                ) {
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        headlineContent = { Text("Meine Informationen") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable {
                            moreMenuExpanded = false
                            navigateTo(MoreRoutes.STUDENT)
                        }
                    )
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        headlineContent = { Text("Mensa") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Restaurant,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable {
                            moreMenuExpanded = false
                            navigateTo(MoreRoutes.MENSA)
                        }
                    )
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        headlineContent = { Text("Absenzen") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.NoAccounts,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable {
                            moreMenuExpanded = false
                            navigateTo(MoreRoutes.ABSENCES)
                        }
                    )

                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        headlineContent = { Text("Einstellungen") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable {
                            moreMenuExpanded = false
                            navigateTo(MoreRoutes.SETTINGS)
                        }
                    )

                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        headlineContent = { Text("Hilfe") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable {
                            moreMenuExpanded = false
                            navigateTo(MoreRoutes.HELP)
                        }
                    )


                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }

        }
    }
}
