package com.thecomingweek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.thecomingweek.ui.navigation.TheComingWeekBottomBar
import com.thecomingweek.ui.navigation.TheComingWeekNavGraph
import com.thecomingweek.ui.theme.TheComingWeekTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheComingWeekTheme {
                AppContent()
            }
        }
    }
}

@Composable
private fun AppContent() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { TheComingWeekBottomBar(navController) },
    ) { padding ->
        TheComingWeekNavGraph(
            navController = navController,
            modifier = Modifier.padding(padding),
        )
    }
}
