package com.thecomingweek.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.thecomingweek.domain.model.Quest
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.QuestType
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.ui.navigation.Route
import com.thecomingweek.ui.theme.TheComingWeekTheme

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Auto-trigger: late hour, today's reckoning unfought — brought to the
    // player before Home has a chance to render normally.
    LaunchedEffect(Unit) {
        viewModel.navigateToBattle.collect {
            navController.navigate(Route.Battle.path(force = false))
        }
    }

    HomeScreenContent(
        today = state.today,
        daysUntilTrial = state.daysUntilTrial,
        currentHp = state.currentHp,
        maxHp = state.maxHp,
        questsDrawn = state.today.isNotEmpty(),
        battleResolved = state.battleResolved,
        rerollsRemaining = state.rerollsRemaining,
        isRerollMode = state.isRerollMode,
        onQuestCompleted = viewModel::onQuestCompleted,
        onInitiateBattle = { force -> navController.navigate(Route.Battle.path(force = force)) },
        onRerollModeToggle = viewModel::onRerollModeToggle,
        onRerollQuest = viewModel::onRerollQuest,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(
    today: List<Quest>,
    daysUntilTrial: Int,
    currentHp: Int,
    maxHp: Int,
    questsDrawn: Boolean,
    battleResolved: Boolean,
    rerollsRemaining: Int,
    isRerollMode: Boolean,
    onQuestCompleted: (Quest) -> Unit,
    onInitiateBattle: (force: Boolean) -> Unit,
    onRerollModeToggle: () -> Unit,
    onRerollQuest: (String) -> Unit,
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = trialLine(daysUntilTrial),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
            Text(
                text = "Wounds: $currentHp / $maxHp",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
            Spacer(Modifier.height(8.dp))
            today.forEach { quest ->
                QuestCard(
                    quest = quest,
                    isRerollMode = isRerollMode,
                    onComplete = { onQuestCompleted(quest) },
                    onReroll = { onRerollQuest(quest.id) },
                )
            }
            Spacer(Modifier.height(8.dp))
            val anyAvailable = today.any { it.status == QuestStatus.AVAILABLE }
            TheFatesButton(
                rerollsRemaining = rerollsRemaining,
                enabled = rerollsRemaining > 0 && anyAvailable,
                onToggle = onRerollModeToggle,
            )
            if (isRerollMode) {
                Text(
                    text = "Choose a quest to replace. Tap to cancel.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRerollModeToggle() },
                )
            }
            Spacer(Modifier.height(8.dp))
            InitiateBattleButton(
                enabled = questsDrawn && !battleResolved,
                onInitiateBattle = onInitiateBattle,
            )
        }
    }
}

@Composable
private fun TheFatesButton(
    rerollsRemaining: Int,
    enabled: Boolean,
    onToggle: () -> Unit,
) {
    Text(
        text = "The Fates ($rerollsRemaining)",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.4f)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary), RectangleShape)
            .then(if (enabled) Modifier.clickable { onToggle() } else Modifier)
            .padding(vertical = 16.dp),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InitiateBattleButton(
    enabled: Boolean,
    onInitiateBattle: (force: Boolean) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Text(
        text = "Initiate Battle",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.4f)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary), RectangleShape)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { if (enabled) onInitiateBattle(false) },
                // Debug bypass: a long press fights today's battle again even if
                // it has already been resolved.
                onLongClick = { onInitiateBattle(true) },
            )
            .padding(vertical = 16.dp),
    )
}

@Composable
private fun QuestCard(
    quest: Quest,
    isRerollMode: Boolean,
    onComplete: () -> Unit,
    onReroll: () -> Unit,
) {
    val completed = quest.status == QuestStatus.COMPLETED
    val available = quest.status == QuestStatus.AVAILABLE

    val cardModifier = Modifier
        .fillMaxWidth()
        .then(
            when {
                isRerollMode && available -> Modifier.clickable { onReroll() }
                isRerollMode -> Modifier
                !completed -> Modifier.clickable { onComplete() }
                else -> Modifier
            }
        )
        .background(MaterialTheme.colorScheme.surface, RectangleShape)
        .border(
            BorderStroke(
                width = 1.dp,
                color = if (isRerollMode && available) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
            ),
            RectangleShape,
        )
        .padding(16.dp)
        // Completing a ritual is solemn: a finished card recedes rather than
        // celebrates. Dimming reads as "observed and set aside". In Reroll
        // Mode the same dimming marks cards that are off-limits to the Fates.
        .alpha(if (completed || (isRerollMode && !available)) 0.4f else 1f)

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
            text = quest.action,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = quest.flavor,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        if (completed) {
            Text(
                text = "✓ Completed",
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
                    action = "Complete a full calisthenics workout session.",
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
                    action = "Read thirty pages of a dense, instructive text.",
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
                    action = "Meditate for twenty minutes in complete stillness.",
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
            currentHp = 12,
            maxHp = 15,
            questsDrawn = true,
            battleResolved = false,
            rerollsRemaining = 3,
            isRerollMode = false,
            onQuestCompleted = {},
            onInitiateBattle = {},
            onRerollModeToggle = {},
            onRerollQuest = {},
        )
    }
}
