package com.thecomingweek.ui.screen.hero

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.thecomingweek.domain.model.Buff
import com.thecomingweek.domain.model.BuffPolarity
import com.thecomingweek.domain.model.BuffSource
import com.thecomingweek.domain.model.Stat
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.ui.navigation.Route
import com.thecomingweek.ui.theme.Bone
import com.thecomingweek.ui.theme.TheComingWeekTheme

@Composable
fun HeroScreen(
    navController: NavHostController,
    viewModel: HeroViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HeroScreenContent(
        level = state.level,
        xp = state.xp,
        xpToNext = state.xpToNext,
        currentHp = state.currentHp,
        maxHp = state.maxHp,
        stats = state.stats,
        activeBuffs = state.activeBuffs,
        onStatClick = { stat -> navController.navigate(Route.StatQuests(stat).path) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeroScreenContent(
    level: Int,
    xp: Int,
    xpToNext: Int,
    currentHp: Int,
    maxHp: Int,
    stats: List<Stat>,
    activeBuffs: List<Buff>,
    onStatClick: (StatType) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Hero",
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
            Text(
                text = "LEVEL $level",
                style = MaterialTheme.typography.displayMedium,
                color = Bone,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            LinearProgressIndicator(
                progress = { if (xpToNext > 0) xp.toFloat() / xpToNext else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surface,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Square,
            )
            Text(
                text = "$xp / $xpToNext XP",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            SpriteSlot()

            HpRow(currentHp = currentHp, maxHp = maxHp)

            SigilDivider()

            if (activeBuffs.isNotEmpty()) {
                BuffChipRow(activeBuffs)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "STATS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = "TAP TO OPEN QUESTS ▸",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            StatGrid(stats = stats, onStatClick = onStatClick)
        }
    }
}

// 32x32 sprite slot, scaled up for visibility. Corner ticks mark the bounds of
// the eventual sprite asset; "@" is the pre-art placeholder.
@Composable
private fun SpriteSlot() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RectangleShape)
                .background(MaterialTheme.colorScheme.surface, RectangleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "@",
                style = MaterialTheme.typography.displaySmall,
                color = Bone,
            )
            CornerTicks()
        }
    }
}

@Composable
private fun BoxScope.CornerTicks() {
    val color = Bone
    val thickness = 2.dp
    val length = 8.dp

    // Top-left
    Box(Modifier.align(Alignment.TopStart).size(length, thickness).background(color))
    Box(Modifier.align(Alignment.TopStart).size(thickness, length).background(color))
    // Top-right
    Box(Modifier.align(Alignment.TopEnd).size(length, thickness).background(color))
    Box(Modifier.align(Alignment.TopEnd).size(thickness, length).background(color))
    // Bottom-left
    Box(Modifier.align(Alignment.BottomStart).size(length, thickness).background(color))
    Box(Modifier.align(Alignment.BottomStart).size(thickness, length).background(color))
    // Bottom-right
    Box(Modifier.align(Alignment.BottomEnd).size(length, thickness).background(color))
    Box(Modifier.align(Alignment.BottomEnd).size(thickness, length).background(color))
}

@Composable
private fun HpRow(currentHp: Int, maxHp: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "HP",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
            Text(
                text = "$currentHp / $maxHp",
                style = MaterialTheme.typography.titleLarge,
                color = Bone,
            )
        }
        LinearProgressIndicator(
            progress = { if (maxHp > 0) currentHp.toFloat() / maxHp else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surface,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Square,
        )
    }
}

@Composable
private fun SigilDivider() {
    Text(
        text = "◆",
        style = MaterialTheme.typography.titleLarge,
        color = Bone,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun BuffChipRow(buffs: List<Buff>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        buffs.forEach { buff -> BuffChip(buff) }
    }
}

@Composable
private fun BuffChip(buff: Buff) {
    val isBuff = buff.polarity == BuffPolarity.BUFF
    val color = if (isBuff) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    val arrow = if (isBuff) "▲" else "▼"
    val sign = if (isBuff) "+" else "-"
    val statAbbrev = buff.statAffected?.name?.take(3) ?: ""
    Text(
        text = "$arrow $statAbbrev $sign${kotlin.math.abs(buff.modifier)}",
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .border(BorderStroke(1.dp, color), RectangleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
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

@Composable
private fun StatGrid(stats: List<Stat>, onStatClick: (StatType) -> Unit) {
    val ordered = StatType.entries.map { type ->
        stats.find { it.type == type } ?: Stat(type = type, value = 0, weeklyGain = 0)
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        ordered.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                row.forEach { stat ->
                    StatCell(stat = stat, modifier = Modifier.weight(1f), onClick = { onStatClick(stat.type) })
                }
            }
        }
    }
}

@Composable
private fun StatCell(stat: Stat, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surface, RectangleShape)
            .border(BorderStroke(1.dp, Bone), RectangleShape)
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stat.type.name.take(3),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = statGlyphs[stat.type] ?: "",
            style = MaterialTheme.typography.titleLarge,
            color = Bone,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stat.value.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = Bone,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HeroScreenPreview() {
    TheComingWeekTheme {
        HeroScreenContent(
            level = 3,
            xp = 140,
            xpToNext = 300,
            currentHp = 12,
            maxHp = 15,
            stats = listOf(
                Stat(StatType.STRENGTH, value = 7, weeklyGain = 2),
                Stat(StatType.AGILITY, value = 3, weeklyGain = 0),
                Stat(StatType.VITALITY, value = 5, weeklyGain = 1),
                Stat(StatType.INTELLECT, value = 9, weeklyGain = 3),
                Stat(StatType.CREATIVITY, value = 2, weeklyGain = 0),
                Stat(StatType.WILLPOWER, value = 6, weeklyGain = 1),
            ),
            activeBuffs = listOf(
                Buff(
                    id = 1L,
                    name = "QUEST_COMPLETED",
                    polarity = BuffPolarity.BUFF,
                    statAffected = StatType.STRENGTH,
                    modifier = 1,
                    expiresEpochDay = 99999,
                    source = BuffSource.QUEST_COMPLETED,
                ),
                Buff(
                    id = 2L,
                    name = "QUEST_MISSED",
                    polarity = BuffPolarity.DEBUFF,
                    statAffected = StatType.VITALITY,
                    modifier = 1,
                    expiresEpochDay = 99999,
                    source = BuffSource.QUEST_MISSED,
                ),
            ),
            onStatClick = {},
        )
    }
}
