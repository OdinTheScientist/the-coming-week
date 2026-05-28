package com.thecomingweek.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.thecomingweek.domain.model.Quest
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.QuestType
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.ui.theme.TheComingWeekTheme

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreenContent(
        today = state.today,
        daysUntilTrial = state.daysUntilTrial,
        onQuestCompleted = viewModel::onQuestCompleted,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    today: List<Quest>,
    daysUntilTrial: Int,
    onQuestCompleted: (Quest) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "The Coming Week",
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
                text = trialLine(daysUntilTrial),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
            Spacer(Modifier.height(8.dp))
            today.forEach { quest ->
                QuestCard(quest = quest, onComplete = { onQuestCompleted(quest) })
            }
        }
    }
}

@Composable
private fun QuestCard(
    quest: Quest,
    onComplete: () -> Unit,
) {
    val completed = quest.status == QuestStatus.COMPLETED

    val cardModifier = Modifier
        .fillMaxWidth()
        .then(if (completed) Modifier else Modifier.clickable { onComplete() })
        .background(MaterialTheme.colorScheme.surface, RectangleShape)
        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RectangleShape)
        .padding(16.dp)
        // Completing a ritual is solemn: a finished card recedes rather than
        // celebrates. Dimming reads as "observed and set aside".
        .alpha(if (completed) 0.45f else 1f)

    Column(
        modifier = cardModifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = quest.stat.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = quest.title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = quest.flavor,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        if (completed) {
            Text(
                text = "✓ observed",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

// Quiet, ritualistic framing of the descent toward Sunday.
private fun trialLine(daysUntilTrial: Int): String = when (daysUntilTrial) {
    0 -> "The Trial is today."
    1 -> "The Trial comes tomorrow."
    else -> "The Trial comes in $daysUntilTrial days."
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TheComingWeekTheme {
        HomeScreenContent(
            today = listOf(
                Quest(
                    id = "str_01_20601",
                    title = "Bear the iron",
                    flavor = "Lift until the body forgets comfort.",
                    stat = StatType.STRENGTH,
                    type = QuestType.DAILY,
                    xpReward = 10,
                    statGain = 1,
                    weight = 10,
                    status = QuestStatus.AVAILABLE,
                    dayAssigned = 20601,
                ),
                Quest(
                    id = "int_01_20601",
                    title = "Read the old pages",
                    flavor = "Thirty minutes given to a difficult text.",
                    stat = StatType.INTELLECT,
                    type = QuestType.DAILY,
                    xpReward = 10,
                    statGain = 1,
                    weight = 10,
                    status = QuestStatus.COMPLETED,
                    dayAssigned = 20601,
                ),
                Quest(
                    id = "wil_01_20601",
                    title = "Sit in the silence",
                    flavor = "Ten minutes of stillness, unbroken.",
                    stat = StatType.WILLPOWER,
                    type = QuestType.DAILY,
                    xpReward = 10,
                    statGain = 1,
                    weight = 10,
                    status = QuestStatus.AVAILABLE,
                    dayAssigned = 20601,
                ),
            ),
            daysUntilTrial = 3,
            onQuestCompleted = {},
        )
    }
}
