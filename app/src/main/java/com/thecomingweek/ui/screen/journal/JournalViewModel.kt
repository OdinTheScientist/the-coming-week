package com.thecomingweek.ui.screen.journal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecomingweek.domain.model.BattleOutcome
import com.thecomingweek.domain.model.DayType
import com.thecomingweek.domain.model.QuestSnapshot
import com.thecomingweek.domain.usecase.GetDayRecordUseCase
import com.thecomingweek.domain.usecase.SaveDayNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDayRecordUseCase: GetDayRecordUseCase,
    private val saveDayNoteUseCase: SaveDayNoteUseCase,
) : ViewModel() {

    data class UiState(
        val epochDay: Long = 0L,
        val displayDate: String = "",
        val dayType: DayType = DayType.TODAY,
        val quests: List<QuestSnapshot> = emptyList(),
        val battleOutcome: BattleOutcome? = null,
        val hpBefore: Int = 0,
        val hpAfter: Int = 0,
        val note: String = "",
        val isEditingNote: Boolean = false,
        val isLoading: Boolean = true,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        val epochDay = savedStateHandle.get<Long>("epochDay") ?: LocalDate.now().toEpochDay()
        val today = LocalDate.now().toEpochDay()
        val dayType = when {
            epochDay < today -> DayType.PAST
            epochDay == today -> DayType.TODAY
            else -> DayType.FUTURE
        }
        val date = LocalDate.ofEpochDay(epochDay)
        val dayName = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        val monthAbbr = date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        val displayDate = "$dayName, $monthAbbr ${date.dayOfMonth}"

        _state.update {
            it.copy(
                epochDay = epochDay,
                dayType = dayType,
                displayDate = displayDate,
                isEditingNote = dayType == DayType.FUTURE,
            )
        }

        viewModelScope.launch {
            val record = getDayRecordUseCase(epochDay)
            _state.update {
                it.copy(
                    quests = record?.quests ?: emptyList(),
                    battleOutcome = record?.battleOutcome,
                    hpBefore = record?.hpBefore ?: 0,
                    hpAfter = record?.hpAfter ?: 0,
                    note = record?.note ?: "",
                    isLoading = false,
                )
            }
        }
    }

    fun onNoteChanged(text: String) {
        _state.update { it.copy(note = text) }
    }

    fun onNoteSaved() {
        val snapshot = _state.value
        viewModelScope.launch {
            saveDayNoteUseCase(snapshot.epochDay, snapshot.note)
            _state.update { it.copy(isEditingNote = false) }
        }
    }

    fun onEditNote() {
        _state.update { it.copy(isEditingNote = true) }
    }
}
