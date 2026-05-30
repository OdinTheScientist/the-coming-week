package com.thecomingweek.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.thecomingweek.ui.screen.biome.BiomeScreen
import com.thecomingweek.ui.screen.boss.BossScreen
import com.thecomingweek.ui.screen.home.HomeScreen
import com.thecomingweek.ui.screen.quest.QuestDetailScreen
import com.thecomingweek.ui.screen.splash.SplashScreen
import com.thecomingweek.ui.screen.stats.StatsScreen
import com.thecomingweek.ui.screen.week.WeekScreen

@Composable
fun TheComingWeekNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash.path,
        modifier = modifier,
    ) {
        composable(Route.Splash.path) {
            SplashScreen(onTimeout = {
                navController.navigate(Route.Home.path) {
                    popUpTo(Route.Splash.path) { inclusive = true }
                }
            })
        }
        composable(Route.Home.path) { HomeScreen(navController) }
        composable(Route.Week.path) { WeekScreen(navController) }
        composable(Route.Stats.path) { StatsScreen(navController) }
        composable(Route.Biome.path) { BiomeScreen(navController) }
        composable(Route.Boss.path) { BossScreen(navController) }
        composable(
            Route.QuestDetail.PATTERN,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            val questId = backStackEntry.arguments?.getString("id") ?: ""
            QuestDetailScreen(navController, questId)
        }
    }
}
