package com.thecomingweek.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.thecomingweek.ui.theme.TheComingWeekTheme

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    // 8a: collecting state activates the reactive pipeline, which opens the DB
    // and fires the first-launch seed. The real quest UI arrives in 8b.
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreenContent(
        questCount = state.today.size,
        onOpenQuest = { navController.navigate("quest/placeholder-1") },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    questCount: Int,
    onOpenQuest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Home",
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
                .clickable { onOpenQuest() },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Today’s three quests await.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                // TODO(Stage 8b): replace proof-of-life with real quest cards.
                Text(
                    text = "$questCount drawn",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TheComingWeekTheme {
        HomeScreenContent(questCount = 3, onOpenQuest = {})
    }
}
