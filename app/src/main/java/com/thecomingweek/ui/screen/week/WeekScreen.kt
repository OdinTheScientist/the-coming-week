package com.thecomingweek.ui.screen.week

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.thecomingweek.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Week",
                        style = MaterialTheme.typography.displaySmall,
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clickable { navController.navigate(Route.Boss.path) },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "The week descends.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
