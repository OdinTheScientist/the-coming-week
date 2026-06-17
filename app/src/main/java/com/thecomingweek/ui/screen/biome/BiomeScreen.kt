package com.thecomingweek.ui.screen.biome

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.thecomingweek.domain.model.CalendarDay
import com.thecomingweek.domain.model.DayState
import com.thecomingweek.ui.navigation.Route
import com.thecomingweek.ui.theme.Ash
import com.thecomingweek.ui.theme.Blood
import com.thecomingweek.ui.theme.Bone
import com.thecomingweek.ui.theme.Ember
import com.thecomingweek.ui.theme.Pitch
import java.time.LocalDate

private val COLUMN_LABELS = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

private val ASCII_BACKDROP = """
                    /\        /\
          __       /  \  /\  /  \
    /\   /  \  /\ / '' \/  \/    \___
   /  \_/ '' \/ \/ ,, , \  / ''  \
  / ,, / ,, ,/\  / ''  , \/  ,,   \
 /____/______/ \/___________/______\

     |"|    |"|    |"|    |"|    |"|
     | |____|_|____|_|____|_|____|_|
     |  ____   ____   ____   ____  |
     | |    | |    | |    | |    | |
     | |    | |    | |    | |    | |
     |_|____|_|____|_|____|_|____|_|

          A monastery of unspoken vows.
""".trimIndent()

@Composable
fun BiomeScreen(
    navController: NavHostController,
    viewModel: BiomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BiomeScreenContent(
        state = state,
        onPaginateBack = viewModel::onPaginateBack,
        onPaginateForward = viewModel::onPaginateForward,
        onDebugDayJump = viewModel::onDebugDayJump,
        onDayTap = { epochDay -> navController.navigate(Route.Journal(epochDay).path) },
    )
}

@Composable
private fun BiomeScreenContent(
    state: BiomeViewModel.UiState,
    onPaginateBack: () -> Unit,
    onPaginateForward: () -> Unit,
    onDebugDayJump: (Long) -> Unit,
    onDayTap: (Long) -> Unit,
) {
    var debugMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Pitch,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(8.dp))

            BiomeHeader(
                state = state,
                onPaginateBack = onPaginateBack,
                onPaginateForward = onPaginateForward,
            )

            Spacer(Modifier.height(12.dp))

            CalendarBox(
                calendarDays = state.calendarDays,
                onTap = onDayTap,
                onLongPress = { epochDay ->
                    onDebugDayJump(epochDay)
                    val date = LocalDate.ofEpochDay(epochDay)
                    debugMessage = "Day set to $date."
                },
            )

            if (debugMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = debugMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = Bone.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BiomeHeader(
    state: BiomeViewModel.UiState,
    onPaginateBack: () -> Unit,
    onPaginateForward: () -> Unit,
) {
    val biomeName = state.displayedBiome?.name ?: "- - -"
    val dateRange = if (state.calendarDays.isNotEmpty()) {
        val start = LocalDate.ofEpochDay(state.calendarDays.first().epochDay)
        val end = LocalDate.ofEpochDay(state.calendarDays.last().epochDay)
        "${start.month.name.take(3)} ${start.dayOfMonth} — ${end.month.name.take(3)} ${end.dayOfMonth}"
    } else ""

    val currentBiome = state.currentBiome
    val displayedBiome = state.displayedBiome
    val weekLabel = if (currentBiome != null && displayedBiome != null && displayedBiome.id == currentBiome.id) {
        val weeksIn = ((LocalDate.now().toEpochDay() - displayedBiome.startEpochDay) / 7 + 1)
            .coerceIn(1, displayedBiome.weekCount.toLong())
        "Week $weeksIn of ${displayedBiome.weekCount}"
    } else if (displayedBiome != null) {
        "Week ${displayedBiome.weekCount} of ${displayedBiome.weekCount}"
    } else {
        "Beyond the veil"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Back chevron
        Text(
            text = if (state.canPaginateBack) "<" else " ",
            style = MaterialTheme.typography.displaySmall,
            color = if (state.canPaginateBack) Bone else Color.Transparent,
            modifier = if (state.canPaginateBack) {
                Modifier.clickable(onClick = onPaginateBack)
            } else Modifier,
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = biomeName,
                style = MaterialTheme.typography.displaySmall,
                color = Bone,
                textAlign = TextAlign.Center,
            )
            Text(
                text = dateRange,
                style = MaterialTheme.typography.bodySmall,
                color = Bone.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
            )
            Text(
                text = weekLabel,
                style = MaterialTheme.typography.bodySmall,
                color = Ember,
                textAlign = TextAlign.Center,
            )
        }

        // Forward chevron
        Text(
            text = if (state.canPaginateForward) ">" else " ",
            style = MaterialTheme.typography.displaySmall,
            color = if (state.canPaginateForward) Bone else Color.Transparent,
            modifier = if (state.canPaginateForward) {
                Modifier.clickable(onClick = onPaginateForward)
            } else Modifier,
        )
    }
}

@Composable
private fun CalendarBox(
    calendarDays: List<CalendarDay>,
    onTap: (Long) -> Unit,
    onLongPress: (Long) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // ASCII backdrop layer
        Text(
            text = ASCII_BACKDROP,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 7.sp),
            color = Bone.copy(alpha = 0.15f),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
            lineHeight = 10.sp,
        )

        // Calendar grid layer
        Column(modifier = Modifier.fillMaxWidth()) {
            // Column headers
            Row(modifier = Modifier.fillMaxWidth()) {
                COLUMN_LABELS.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = Bone.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Anchor the grid to the Monday on or before the first day so that
            // Sundays always fall under the SUN column header. Mon=1..Sun=7 in
            // DayOfWeek.value, so offset = value - 1 gives Mon=0 .. Sun=6.
            val leadingEmpties = if (calendarDays.isEmpty()) 0 else {
                LocalDate.ofEpochDay(calendarDays.first().epochDay).dayOfWeek.value - 1
            }
            // Null slots are leading padding; non-null slots are real days.
            val gridItems: List<CalendarDay?> =
                List(leadingEmpties) { null } + calendarDays

            gridItems.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    week.forEach { day ->
                        if (day == null) {
                            Spacer(
                                Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        } else {
                            DayCell(
                                day = day,
                                onTap = onTap,
                                onLongPress = onLongPress,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    // Pad incomplete last row
                    repeat(7 - week.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DayCell(
    day: CalendarDay,
    onTap: (Long) -> Unit,
    onLongPress: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (bg, border, textColor, suffix) = dayStyle(day.state)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(bg, RectangleShape)
            .border(1.dp, border, RectangleShape)
            .combinedClickable(
                onClick = { onTap(day.epochDay) },
                onLongClick = { onLongPress(day.epochDay) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (day.monthLabel != null) {
                Text(
                    text = day.monthLabel,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 6.sp),
                    color = Ember,
                    lineHeight = 7.sp,
                )
            }
            Text(
                text = day.dayOfMonth.toString() + (suffix ?: ""),
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 12.sp),
                color = textColor,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
            )
        }
    }
}

private data class DayStyleTokens(
    val bg: Color,
    val border: Color,
    val text: Color,
    val suffix: String?,
)

private fun dayStyle(state: DayState): DayStyleTokens = when (state) {
    DayState.FUTURE -> DayStyleTokens(Pitch, Ash.copy(alpha = 0.4f), Ash, null)
    DayState.TODAY -> DayStyleTokens(Ash, Ember, Ember, null)
    DayState.COMPLETE -> DayStyleTokens(Ash, Bone, Bone, "✦")
    DayState.PARTIAL -> DayStyleTokens(Ash, Bone, Bone, "◆")
    DayState.MISSED -> DayStyleTokens(Ash, Blood.copy(alpha = 0.5f), Blood, null)
    DayState.BOSS -> DayStyleTokens(Ash, Blood, Bone, "⚔")
    DayState.BOSS_WON -> DayStyleTokens(Ash, Ember, Ember, "✦")
    DayState.BOSS_LOST -> DayStyleTokens(Ash, Blood, Blood, "✕")
}
