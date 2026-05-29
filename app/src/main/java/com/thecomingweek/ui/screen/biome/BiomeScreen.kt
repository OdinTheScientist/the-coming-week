package com.thecomingweek.ui.screen.biome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.thecomingweek.ui.theme.TheComingWeekTheme

@Composable
fun BiomeScreen(
    navController: NavHostController,
    viewModel: BiomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BiomeScreenContent(
        name = state.name,
        flavor = state.flavor,
        weekNumber = state.weekNumber,
        weekCount = state.weekCount,
        runNumber = state.runNumber,
        hasBiome = state.hasBiome,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BiomeScreenContent(
    name: String,
    flavor: String,
    weekNumber: Int,
    weekCount: Int,
    runNumber: Int,
    hasBiome: Boolean,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (hasBiome) name else "The Land",
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

            if (!hasBiome) {
                Text(
                    text = "No land has yet risen to meet you.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                )
                return@Column
            }

            Text(
                text = flavor,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Week $weekNumber of $weekCount",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(Modifier.weight(1f))

            // The run, kept quiet at the foot of the screen. Each descent is
            // numbered; stats carry over, the land begins again.
            Text(
                text = "Descent ${toRoman(runNumber)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

// Small Roman numeral for the descent count. Runs stay small in practice; for
// anything beyond the table we fall back to the plain number rather than fail.
private fun toRoman(n: Int): String {
    if (n <= 0) return n.toString()
    val numerals = listOf(
        1000 to "M", 900 to "CM", 500 to "D", 400 to "CD",
        100 to "C", 90 to "XC", 50 to "L", 40 to "XL",
        10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I",
    )
    var remaining = n
    val sb = StringBuilder()
    for ((value, symbol) in numerals) {
        while (remaining >= value) {
            sb.append(symbol)
            remaining -= value
        }
    }
    return sb.toString()
}

@Preview(showBackground = true)
@Composable
private fun BiomeScreenPreview() {
    TheComingWeekTheme {
        BiomeScreenContent(
            name = "The Stone Hours",
            flavor = "A monastery of unspoken vows.",
            weekNumber = 3,
            weekCount = 6,
            runNumber = 2,
            hasBiome = true,
        )
    }
}
