package com.thecomingweek.ui.screen.statquests

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.thecomingweek.domain.model.Quest
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.QuestType
import com.thecomingweek.domain.model.StatQuestEntry
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.ui.theme.Bone
import com.thecomingweek.ui.theme.TheComingWeekTheme

@Composable
fun StatQuestsScreen(
    navController: NavHostController,
    viewModel: StatQuestsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StatQuestsScreenContent(
        stat = state.stat,
        statValue = state.statValue,
        quotaDone = state.quotaDone,
        quotaTotal = state.quotaTotal,
        entries = state.entries,
        onBack = { navController.popBackStack() },
    )
}

private val statGlyphs = mapOf(
    StatType.STRENGTH to "▲",
    StatType.AGILITY to "◆",
    StatType.VITALITY to "+",
    StatType.INTELLECT to "◇",
    StatType.CREATIVITY to "✦",
    StatType.WILLPOWER to "○",
)

private val statSubtitles = mapOf(
    StatType.STRENGTH to "The body is the first fortress.",
    StatType.AGILITY to "To move like water is to evade the blade of fate.",
    StatType.VITALITY to "The vessel must be maintained.",
    StatType.INTELLECT to "The wisdom of the dead is the only map for the living.",
    StatType.CREATIVITY to "Give form to the phantoms that dwell within the mind.",
    StatType.WILLPOWER to "The loudest demons are silenced by the absence of sound.",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatQuestsScreenContent(
    stat: StatType,
    statValue: Int,
    quotaDone: Int,
    quotaTotal: Int,
    entries: List<StatQuestEntry>,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = statGlyphs[stat] ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                color = Bone,
                            )
                            Text(
                                text = stat.name,
                                style = MaterialTheme.typography.displaySmall,
                                color = Bone,
                            )
                        }
                        Text(
                            text = statValue.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
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
                text = statSubtitles[stat] ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
            Text(
                text = "Quota: $quotaDone / $quotaTotal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(entries, key = { it.quest.id }) { entry ->
                    StatQuestCard(entry)
                }
            }
        }
    }
}

@Composable
private fun StatQuestCard(entry: StatQuestEntry) {
    val quest = entry.quest
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RectangleShape)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RectangleShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = quest.stat.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = quest.title,
            style = MaterialTheme.typography.titleLarge,
            color = Bone,
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
        if (entry.isDrawnThisWeek) {
            Text(
                text = "◈ Drawn this week",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatQuestsScreenPreview() {
    TheComingWeekTheme {
        StatQuestsScreenContent(
            stat = StatType.STRENGTH,
            statValue = 7,
            quotaDone = 1,
            quotaTotal = 3,
            entries = listOf(
                StatQuestEntry(
                    quest = Quest(
                        id = "str_01",
                        title = "The Iron Rite",
                        action = "Complete a full calisthenics workout session.",
                        flavor = "The body is the first fortress. Strengthen its walls.",
                        stat = StatType.STRENGTH,
                        type = QuestType.DAILY,
                        xpReward = 10,
                        statGain = 1,
                        weight = 3,
                        status = QuestStatus.AVAILABLE,
                        dayAssigned = null,
                    ),
                    isDrawnThisWeek = true,
                ),
                StatQuestEntry(
                    quest = Quest(
                        id = "str_02",
                        title = "Burden Of Bone",
                        action = "Perform fifty push-ups before the sun reaches its zenith.",
                        flavor = "Pressure forces carbon into diamond, and flesh into steel.",
                        stat = StatType.STRENGTH,
                        type = QuestType.DAILY,
                        xpReward = 10,
                        statGain = 1,
                        weight = 3,
                        status = QuestStatus.AVAILABLE,
                        dayAssigned = null,
                    ),
                    isDrawnThisWeek = false,
                ),
            ),
            onBack = {},
        )
    }
}
