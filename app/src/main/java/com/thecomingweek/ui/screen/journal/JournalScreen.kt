package com.thecomingweek.ui.screen.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.thecomingweek.domain.model.BattleOutcome
import com.thecomingweek.domain.model.DayType
import com.thecomingweek.domain.model.QuestSnapshot
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.ui.theme.Ash
import com.thecomingweek.ui.theme.Blood
import com.thecomingweek.ui.theme.Bone
import com.thecomingweek.ui.theme.Ember
import com.thecomingweek.ui.theme.Pitch

private val statGlyphs = mapOf(
    StatType.STRENGTH to "▲",
    StatType.AGILITY to "◆",
    StatType.VITALITY to "+",
    StatType.INTELLECT to "◇",
    StatType.CREATIVITY to "✦",
    StatType.WILLPOWER to "○",
)

@Composable
fun JournalScreen(
    navController: NavHostController,
    viewModel: JournalViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    JournalScreenContent(
        state = state,
        onBack = { navController.popBackStack() },
        onNoteChanged = viewModel::onNoteChanged,
        onNoteSaved = viewModel::onNoteSaved,
        onEditNote = viewModel::onEditNote,
    )
}

@Composable
private fun JournalScreenContent(
    state: JournalViewModel.UiState,
    onBack: () -> Unit,
    onNoteChanged: (String) -> Unit,
    onNoteSaved: () -> Unit,
    onEditNote: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Pitch,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(8.dp))

            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Bone,
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val parts = state.displayDate.split(", ", limit = 2)
                    Text(
                        text = parts.getOrElse(0) { state.displayDate },
                        style = MaterialTheme.typography.displaySmall,
                        color = Bone,
                        textAlign = TextAlign.Center,
                    )
                    if (parts.size == 2) {
                        Text(
                            text = parts[1],
                            style = MaterialTheme.typography.bodySmall,
                            color = Ember,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                // Invisible placeholder to keep header centered
                Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                    Spacer(Modifier.height(48.dp))
                }
            }

            // Sigil divider
            Text(
                text = "◆",
                style = MaterialTheme.typography.bodyMedium,
                color = Bone.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            when (state.dayType) {
                DayType.FUTURE -> FutureLayout(
                    note = state.note,
                    isEditingNote = state.isEditingNote,
                    onNoteChanged = onNoteChanged,
                    onNoteSaved = onNoteSaved,
                    onEditNote = onEditNote,
                )
                DayType.TODAY -> TodayLayout(
                    quests = state.quests,
                    battleOutcome = state.battleOutcome,
                    hpBefore = state.hpBefore,
                    hpAfter = state.hpAfter,
                    note = state.note,
                    isEditingNote = state.isEditingNote,
                    onNoteChanged = onNoteChanged,
                    onNoteSaved = onNoteSaved,
                    onEditNote = onEditNote,
                )
                DayType.PAST -> PastLayout(
                    quests = state.quests,
                    battleOutcome = state.battleOutcome,
                    hpBefore = state.hpBefore,
                    hpAfter = state.hpAfter,
                    note = state.note,
                    isEditingNote = state.isEditingNote,
                    onNoteChanged = onNoteChanged,
                    onNoteSaved = onNoteSaved,
                    onEditNote = onEditNote,
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PastLayout(
    quests: List<QuestSnapshot>,
    battleOutcome: BattleOutcome?,
    hpBefore: Int,
    hpAfter: Int,
    note: String,
    isEditingNote: Boolean,
    onNoteChanged: (String) -> Unit,
    onNoteSaved: () -> Unit,
    onEditNote: () -> Unit,
) {
    SectionHeader("RITES")
    Spacer(Modifier.height(8.dp))
    QuestList(quests)
    Spacer(Modifier.height(16.dp))

    SectionHeader("THE RECKONING")
    Spacer(Modifier.height(8.dp))
    ReckoningBlock(battleOutcome = battleOutcome, hpBefore = hpBefore, hpAfter = hpAfter)
    Spacer(Modifier.height(16.dp))

    SectionHeader("NOTES")
    Spacer(Modifier.height(8.dp))
    NoteSection(
        note = note,
        isEditingNote = isEditingNote,
        autoFocus = false,
        onNoteChanged = onNoteChanged,
        onNoteSaved = onNoteSaved,
        onEditNote = onEditNote,
    )
}

@Composable
private fun TodayLayout(
    quests: List<QuestSnapshot>,
    battleOutcome: BattleOutcome?,
    hpBefore: Int,
    hpAfter: Int,
    note: String,
    isEditingNote: Boolean,
    onNoteChanged: (String) -> Unit,
    onNoteSaved: () -> Unit,
    onEditNote: () -> Unit,
) {
    SectionHeader("RITES")
    Spacer(Modifier.height(8.dp))
    QuestList(quests)
    Spacer(Modifier.height(16.dp))

    SectionHeader("THE RECKONING")
    Spacer(Modifier.height(8.dp))
    if (battleOutcome == null) {
        Text(
            text = "The hour has not yet come.",
            style = MaterialTheme.typography.bodyMedium,
            color = Bone.copy(alpha = 0.5f),
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
        )
    } else {
        ReckoningBlock(battleOutcome = battleOutcome, hpBefore = hpBefore, hpAfter = hpAfter)
    }
    Spacer(Modifier.height(16.dp))

    SectionHeader("NOTES")
    Spacer(Modifier.height(8.dp))
    NoteSection(
        note = note,
        isEditingNote = isEditingNote,
        autoFocus = false,
        onNoteChanged = onNoteChanged,
        onNoteSaved = onNoteSaved,
        onEditNote = onEditNote,
    )
}

@Composable
private fun FutureLayout(
    note: String,
    isEditingNote: Boolean,
    onNoteChanged: (String) -> Unit,
    onNoteSaved: () -> Unit,
    onEditNote: () -> Unit,
) {
    Text(
        text = "The rites are not yet written.",
        style = MaterialTheme.typography.bodyMedium,
        color = Bone.copy(alpha = 0.5f),
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(16.dp))

    SectionHeader("NOTES")
    Spacer(Modifier.height(8.dp))
    NoteSection(
        note = note,
        isEditingNote = isEditingNote,
        autoFocus = isEditingNote,
        onNoteChanged = onNoteChanged,
        onNoteSaved = onNoteSaved,
        onEditNote = onEditNote,
    )
}

@Composable
private fun QuestList(quests: List<QuestSnapshot>) {
    if (quests.isEmpty()) {
        Text(
            text = "No rites recorded.",
            style = MaterialTheme.typography.bodySmall,
            color = Bone.copy(alpha = 0.5f),
        )
        return
    }
    quests.forEach { snapshot ->
        QuestRow(snapshot)
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun QuestRow(snapshot: QuestSnapshot) {
    val glyph = statGlyphs[snapshot.stat] ?: "◆"
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = glyph,
                style = MaterialTheme.typography.bodyMedium,
                color = Bone,
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(
                text = snapshot.title,
                style = MaterialTheme.typography.displaySmall,
                color = Bone,
            )
        }
        Text(
            text = snapshot.action,
            style = MaterialTheme.typography.bodySmall,
            color = Bone,
            modifier = Modifier.padding(start = 24.dp),
        )
        val (statusText, statusColor) = when (snapshot.status) {
            QuestStatus.COMPLETED -> "✦ Completed" to Ember
            QuestStatus.MISSED -> "✕ Missed" to Blood
            QuestStatus.AVAILABLE -> "◌ Unanswered" to Bone.copy(alpha = 0.5f)
        }
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodySmall,
            color = statusColor,
            modifier = Modifier.padding(start = 24.dp),
        )
    }
}

@Composable
private fun ReckoningBlock(
    battleOutcome: BattleOutcome?,
    hpBefore: Int,
    hpAfter: Int,
) {
    val outcomeText = when (battleOutcome) {
        BattleOutcome.VICTORY -> "Victory"
        BattleOutcome.WOUNDED -> "Wounded"
        null -> "—"
    }
    Text(
        text = outcomeText,
        style = MaterialTheme.typography.displaySmall,
        color = Bone,
    )
    Text(
        text = "Wounds: $hpBefore → $hpAfter",
        style = MaterialTheme.typography.bodyMedium,
        color = Bone,
    )
}

@Composable
private fun NoteSection(
    note: String,
    isEditingNote: Boolean,
    autoFocus: Boolean,
    onNoteChanged: (String) -> Unit,
    onNoteSaved: () -> Unit,
    onEditNote: () -> Unit,
) {
    if (isEditingNote) {
        val focusRequester = remember { FocusRequester() }
        BasicTextField(
            value = note,
            onValueChange = onNoteChanged,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Bone),
            modifier = Modifier
                .fillMaxWidth()
                .background(Ash, RectangleShape)
                .padding(12.dp)
                .then(if (autoFocus) Modifier.focusRequester(focusRequester) else Modifier),
            minLines = 3,
        )
        if (autoFocus) {
            LaunchedEffect(Unit) { focusRequester.requestFocus() }
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .background(Ash, RectangleShape)
                .clickable(onClick = onNoteSaved)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.bodyMedium,
                color = Ember,
            )
        }
    } else {
        if (note.isNotEmpty()) {
            Text(
                text = note,
                style = MaterialTheme.typography.bodyMedium,
                color = Bone,
            )
        } else {
            Text(
                text = "—",
                style = MaterialTheme.typography.bodyMedium,
                color = Bone.copy(alpha = 0.5f),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Edit",
            style = MaterialTheme.typography.bodySmall,
            color = Ember,
            modifier = Modifier.clickable(onClick = onEditNote),
        )
    }
}

@Composable
private fun SectionHeader(label: String) {
    Text(
        text = "── $label ──────────────────────",
        style = MaterialTheme.typography.bodySmall,
        color = Bone.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth(),
    )
}
