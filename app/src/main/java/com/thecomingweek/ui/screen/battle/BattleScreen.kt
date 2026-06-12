package com.thecomingweek.ui.screen.battle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.thecomingweek.domain.model.BattleOutcome
import com.thecomingweek.domain.model.BattleRound
import com.thecomingweek.ui.navigation.Route
import com.thecomingweek.ui.theme.TheComingWeekTheme

@Composable
fun BattleScreen(
    navController: NavHostController,
    viewModel: BattleViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BattleScreenContent(
        enemyName = state.enemyName,
        playerHp = state.playerHp,
        playerMaxHp = state.playerMaxHp,
        enemyHp = state.enemyHp,
        enemyMaxHp = state.enemyMaxHp,
        rounds = state.rounds,
        outcome = state.outcome,
        isLoading = state.isLoading,
        onContinue = { navController.popBackStack(Route.Home.path, inclusive = false) },
    )
}

@Composable
private fun BattleScreenContent(
    enemyName: String,
    playerHp: Int,
    playerMaxHp: Int,
    enemyHp: Int,
    enemyMaxHp: Int,
    rounds: List<BattleRound>,
    outcome: BattleOutcome?,
    isLoading: Boolean,
    onContinue: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "The Reckoning",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))

            if (isLoading) {
                return@Column
            }

            // Hero left, enemy right — the two combatants facing each other.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Combatant(name = "You", glyph = "@", hp = playerHp, maxHp = playerMaxHp, fillColor = MaterialTheme.colorScheme.onBackground)
                Combatant(name = enemyName, glyph = "#", hp = enemyHp, maxHp = enemyMaxHp, fillColor = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "The exchange:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface, RectangleShape)
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RectangleShape)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(rounds) { round ->
                    Column {
                        Text(
                            text = "You strike for ${round.playerDamage}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (round.enemyDamage != null) {
                            Text(
                                text = "$enemyName strikes for ${round.enemyDamage}.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (outcome != null) {
                Text(
                    text = resolutionLine(outcome),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))
            }

            Text(
                text = "Continue",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary), RectangleShape)
                    .clickable { onContinue() }
                    .padding(vertical = 16.dp),
            )
        }
    }
}

private fun resolutionLine(outcome: BattleOutcome): String = when (outcome) {
    BattleOutcome.VICTORY -> "The enemy falls."
    BattleOutcome.WOUNDED -> "You are broken. The week is not over."
}

@Composable
private fun Combatant(name: String, glyph: String, hp: Int, maxHp: Int, fillColor: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.surface, RectangleShape)
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RectangleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = glyph,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(4.dp))
        HpBar(hp = hp, maxHp = maxHp, fillColor = fillColor)
        Text(
            text = "$hp / $maxHp",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun HpBar(hp: Int, maxHp: Int, fillColor: androidx.compose.ui.graphics.Color) {
    val fraction = if (maxHp > 0) (hp.toFloat() / maxHp).coerceIn(0f, 1f) else 0f
    Box(
        modifier = Modifier
            .width(96.dp)
            .height(8.dp)
            .background(MaterialTheme.colorScheme.surface, RectangleShape)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RectangleShape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction)
                .background(fillColor, RectangleShape),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BattleScreenPreview() {
    TheComingWeekTheme {
        BattleScreenContent(
            enemyName = "The Hollow Grunt",
            playerHp = 8,
            playerMaxHp = 15,
            enemyHp = 0,
            enemyMaxHp = 9,
            rounds = listOf(
                BattleRound(1, playerDamage = 4, enemyDamage = 3, playerHpAfter = 12, enemyHpAfter = 5),
                BattleRound(2, playerDamage = 4, enemyDamage = 3, playerHpAfter = 9, enemyHpAfter = 1),
                BattleRound(3, playerDamage = 4, enemyDamage = null, playerHpAfter = 9, enemyHpAfter = 0),
            ),
            outcome = BattleOutcome.VICTORY,
            isLoading = false,
            onContinue = {},
        )
    }
}
