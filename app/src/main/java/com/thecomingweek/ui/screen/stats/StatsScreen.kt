package com.thecomingweek.ui.screen.stats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.thecomingweek.domain.model.Stat
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.ui.theme.TheComingWeekTheme

@Composable
fun StatsScreen(
    navController: NavHostController,
    viewModel: StatsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StatsScreenContent(stats = state.stats)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsScreenContent(stats: List<Stat>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Stats",
                        style = MaterialTheme.typography.displaySmall,
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "The shape of your discipline.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
            Spacer(Modifier.height(8.dp))
            stats.forEach { stat -> StatRow(stat) }
        }
    }
}

@Composable
private fun StatRow(stat: Stat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RectangleShape)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RectangleShape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stat.type.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = weeklyGainLine(stat.weeklyGain),
                style = MaterialTheme.typography.labelSmall,
                color = if (stat.weeklyGain > 0) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                },
            )
        }
        Text(
            text = stat.value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// Weekly growth, framed as accumulation toward the Trial rather than reward.
private fun weeklyGainLine(weeklyGain: Int): String =
    if (weeklyGain > 0) "+$weeklyGain this week" else "untouched this week"

@Preview(showBackground = true)
@Composable
private fun StatsScreenPreview() {
    TheComingWeekTheme {
        StatsScreenContent(
            stats = listOf(
                Stat(StatType.STRENGTH, value = 7, weeklyGain = 2),
                Stat(StatType.AGILITY, value = 3, weeklyGain = 0),
                Stat(StatType.VITALITY, value = 5, weeklyGain = 1),
                Stat(StatType.INTELLECT, value = 9, weeklyGain = 3),
                Stat(StatType.CREATIVITY, value = 2, weeklyGain = 0),
                Stat(StatType.WILLPOWER, value = 6, weeklyGain = 1),
            ),
        )
    }
}
