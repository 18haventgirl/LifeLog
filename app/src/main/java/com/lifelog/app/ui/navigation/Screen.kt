package com.lifelog.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Today
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Record : Screen("record")
    data object DailyReport : Screen("daily_report")
    data object WeeklyReport : Screen("weekly_report")
    data object MonthlyReport : Screen("monthly_report/{year}/{month}") {
        fun createRoute(year: Int, month: Int) = "monthly_report/$year/$month"
    }
    data object Goal : Screen("goal")
    data object Category : Screen("category")
    data object Settings : Screen("settings")
}

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    data object Record : BottomNavItem(Screen.Record.route, Icons.Default.Timer, "记录")
    data object Daily : BottomNavItem(Screen.DailyReport.route, Icons.Default.Today, "日报")
    data object Weekly : BottomNavItem(Screen.WeeklyReport.route, Icons.Default.DateRange, "周报")
    data object Settings : BottomNavItem(Screen.Settings.route, Icons.Default.Settings, "设置")
}
