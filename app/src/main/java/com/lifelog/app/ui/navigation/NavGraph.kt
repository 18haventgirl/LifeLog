package com.lifelog.app.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lifelog.app.ui.screen.record.RecordScreen
import com.lifelog.app.ui.screen.report.DailyReportScreen
import com.lifelog.app.ui.screen.report.MonthlyReportScreen
import com.lifelog.app.ui.screen.report.WeeklyReportScreen
import com.lifelog.app.ui.screen.settings.SettingsScreen
import com.lifelog.app.ui.screen.goal.GoalScreen
import com.lifelog.app.ui.screen.category.CategoryScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Record.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) },
        exitTransition = { fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) }
    ) {
        composable(Screen.Record.route) {
            RecordScreen(navController = navController)
        }

        composable(Screen.DailyReport.route) {
            DailyReportScreen(navController = navController)
        }

        composable(Screen.WeeklyReport.route) {
            WeeklyReportScreen()
        }

        composable(
            route = Screen.MonthlyReport.route,
            arguments = listOf(
                navArgument("year") { type = NavType.IntType },
                navArgument("month") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val year = backStackEntry.arguments?.getInt("year") ?: 2024
            val month = backStackEntry.arguments?.getInt("month") ?: 1
            MonthlyReportScreen(year = year, month = month)
        }

        composable(Screen.Goal.route) {
            GoalScreen()
        }

        composable(Screen.Category.route) {
            CategoryScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
