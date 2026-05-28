package com.thecomingweek.ui.screen.week

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.thecomingweek.domain.model.QuotaProgress
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.ui.navigation.Route
import com.thecomingweek.ui.theme.TheComingWeekTheme

@Composable
fun WeekScreen(
    navController: NavHostController,
    viewModel: WeekViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.events.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    WeekScreenContent(
        weekNumber = state.weekNumber,
        statTheme = state.statTheme,
        quotas = state.quotas,
        allMet = state.allMet,
        onAdvanceWeek = viewModel::onAdvanceWeek,
        onEnterTrial = { navController.navigate(Route.Boss.path) },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun WeekScreenContent(
    weekNumber: Int,
    statTheme: StatType?,
    quotas: List<QuotaProgress>,
    allMet: Boolean,
    onAdvanceWeek: () -> Unit,
    onEnterTrial: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (weekNumber > 0) "Week $weekNumber" else "The Week",
                        style = MaterialTheme.typography.displaySmall,
                        // Hidden dev affordance: long-press the title to advance the
                        // week. Indication is nulled so nothing hints it exists.
                        // The week truly turns when the Trial passes; see WeekViewModel.
                        modifier = Modifier.combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                            onLongClick = onAdvanceWeek,
                        ),
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

            if (statTheme == null) {
                Text(
                    text = "The week has not yet begun.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                )
                return@Column
            }

            Text(
                text = "This week bends toward ${statTheme.name}.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "The week demands:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )

            quotas.forEach { quota -> QuotaRow(quota) }

            Text(
                text = if (allMet) "All demands answered." else "The week remains unanswered.",
                style = MaterialTheme.typography.bodySmall,
                color = if (allMet) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                },
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = "Face the Trial.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEnterTrial() }
                    .padding(vertical = 12.dp),
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun QuotaRow(quota: QuotaProgress) {
    val color = if (quota.met) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RectangleShape)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RectangleShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = quota.stat.name,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${quota.completed}/${quota.required}",
            style = MaterialTheme.typography.bodyMedium,
            color = color,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekScreenPreview() {
    TheComingWeekTheme {
        WeekScreenContent(
            weekNumber = 1,
            statTheme = StatType.STRENGTH,
            quotas = listOf(
                QuotaProgress(StatType.STRENGTH, completed = 2, required = 3),
                QuotaProgress(StatType.AGILITY, completed = 1, required = 1),
                QuotaProgress(StatType.VITALITY, completed = 0, required = 2),
                QuotaProgress(StatType.INTELLECT, completed = 1, required = 1),
                QuotaProgress(StatType.CREATIVITY, completed = 0, required = 1),
                QuotaProgress(StatType.WILLPOWER, completed = 1, required = 1),
            ),
            allMet = false,
            onAdvanceWeek = {},
            onEnterTrial = {},
        )
    }
}
