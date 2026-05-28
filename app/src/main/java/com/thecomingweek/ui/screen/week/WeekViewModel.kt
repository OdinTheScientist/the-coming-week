package com.thecomingweek.ui.screen.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecomingweek.data.repository.QuestRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.QuotaProgress
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.domain.usecase.AdvanceWeekUseCase
import com.thecomingweek.domain.usecase.CheckWeeklyQuotasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeekViewModel @Inject constructor(
    private val weekRepository: WeekRepository,
    private val questRepository: QuestRepository,
    private val checkWeeklyQuotas: CheckWeeklyQuotasUseCase,
    private val advanceWeek: AdvanceWeekUseCase,
) : ViewModel() {

    data class UiState(
        val hasWeek: Boolean = false,
        val weekNumber: Int = 0,
        val statTheme: StatType? = null,
        val quotas: List<QuotaProgress> = emptyList(),
        val allMet: Boolean = false,
        val isLoading: Boolean = true,
    )

    // Reactive on two sources: the current week (so turning the week refreshes
    // the screen) and the quest table (so completing a quest refreshes quota
    // progress). The suspend quota check recomputes against the latest week on
    // each emission; the quest Flow serves as the change trigger.
    val state: StateFlow<UiState> =
        combine(
            weekRepository.observeCurrent(),
            questRepository.observeAll(),
        ) { week, _ ->
            if (week == null) {
                UiState(hasWeek = false, isLoading = false)
            } else {
                val report = checkWeeklyQuotas(week)
                UiState(
                    hasWeek = true,
                    weekNumber = week.weekNumber,
                    statTheme = week.statTheme,
                    quotas = report.progress,
                    allMet = report.allMet,
                    isLoading = false,
                )
            }
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                UiState(isLoading = true),
            )

    // One-shot, plain-text confirmations for the hidden dev advance trigger.
    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAdvanceWeek() {
        viewModelScope.launch {
            // TODO(Stage 10): replace with post-Trial-resolution advancement
            advanceWeek()
            weekRepository.current()?.let { week ->
                _events.send("Advanced to week ${week.weekNumber}")
            }
        }
    }
}
