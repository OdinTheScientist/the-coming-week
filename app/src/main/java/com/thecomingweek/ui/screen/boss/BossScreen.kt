package com.thecomingweek.ui.screen.boss

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.thecomingweek.domain.model.Boss
import com.thecomingweek.domain.model.TrialResult
import com.thecomingweek.ui.navigation.Route
import com.thecomingweek.ui.theme.TheComingWeekTheme

@Composable
fun BossScreen(
    navController: NavHostController,
    viewModel: BossViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BossScreenContent(
        boss = state.boss,
        finalDifficulty = state.finalDifficulty,
        playerScore = state.playerScore,
        result = state.result,
        isLoading = state.isLoading,
        onFaceTrial = viewModel::onFaceTrial,
        // The single way back out of the Trial: returns to Home, which now shows
        // the turned week. Pops the Boss (and the Week beneath it) off the stack.
        onContinue = { navController.popBackStack(Route.Home.path, inclusive = false) },
    )
}

@Composable
private fun BossScreenContent(
    boss: Boss?,
    finalDifficulty: Int,
    playerScore: Int,
    result: TrialResult?,
    isLoading: Boolean,
    onFaceTrial: () -> Unit,
    onContinue: () -> Unit,
) {
    // Sunday's distinct treatment: stripped chrome (no TopAppBar, no bottom bar)
    // and a blood frame standing in for the vignette — the screen bleeds at its
    // edges. A true radial vignette is a gradient, which the flat aesthetic
    // forbids elsewhere; the frame keeps the dread without breaking the rule.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .border(BorderStroke(4.dp, MaterialTheme.colorScheme.primary), RectangleShape)
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "THE TRIAL",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(48.dp))

            if (isLoading) {
                // Nothing to announce yet. Silence suits the moment.
                return@Column
            }

            if (boss == null) {
                Text(
                    text = "The week has not come. There is nothing here to face.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
                return@Column
            }

            Text(
                text = boss.name,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = boss.flavor,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(40.dp))

            Reckoning(label = "The Trial weighs", value = finalDifficulty)
            Spacer(Modifier.height(8.dp))
            Reckoning(label = "You bring", value = playerScore)

            Spacer(Modifier.weight(1f))

            if (result == null) {
                TrialAction(text = "Face it.", onClick = onFaceTrial)
            } else {
                Text(
                    text = outcomeLine(result.defeated),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(24.dp))
                TrialAction(text = "Continue", onClick = onContinue)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// Acknowledgment, not celebration or commiseration. The Trial is stated; the
// player is left to sit with it.
private fun outcomeLine(defeated: Boolean): String =
    if (defeated) "The Trial passes. You remain." else "The Trial breaks you. The week comes again."

@Composable
private fun Reckoning(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun TrialAction(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary), RectangleShape)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun BossScreenPreview() {
    TheComingWeekTheme {
        BossScreenContent(
            boss = Boss(
                id = 1,
                weekId = 1,
                biomeId = 1,
                name = "The Warden of the Stone Hours",
                flavor = "It has kept the week. Now it asks what you made of it.",
                baseDifficulty = 10,
                finalDifficulty = 13,
                defeated = null,
            ),
            finalDifficulty = 13,
            playerScore = 24,
            result = null,
            isLoading = false,
            onFaceTrial = {},
            onContinue = {},
        )
    }
}
