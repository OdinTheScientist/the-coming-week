package com.thecomingweek.ui.screen.statquests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecomingweek.data.repository.StatRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.StatQuestEntry
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.domain.usecase.CheckWeeklyQuotasUseCase
import com.thecomingweek.domain.usecase.GetQuestsByStatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatQuestsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getQuestsByStat: GetQuestsByStatUseCase,
    private val checkWeeklyQuotas: CheckWeeklyQuotasUseCase,
    private val statRepository: StatRepository,
    private val weekRepository: WeekRepository,
) : ViewModel() {

    data class UiState(
        val stat: StatType = StatType.STRENGTH,
        val statValue: Int = 0,
        val quotaDone: Int = 0,
        val quotaTotal: Int = 0,
        val entries: List<StatQuestEntry> = emptyList(),
        val isLoading: Boolean = true,
    )

    private val stat: StatType = StatType.valueOf(
        savedStateHandle.get<String>("stat") ?: StatType.STRENGTH.name
    )
    private val epochDay: Long = LocalDate.now().toEpochDay()

    private val _state = MutableStateFlow(UiState(stat = stat, isLoading = true))
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val entries = getQuestsByStat(stat, epochDay)
            val statValue = statRepository.all().find { it.type == stat }?.value ?: 0

            val week = weekRepository.current()
            val quota = week?.let { checkWeeklyQuotas(it) }
                ?.progress
                ?.find { it.stat == stat }

            _state.value = UiState(
                stat = stat,
                statValue = statValue,
                quotaDone = quota?.completed ?: 0,
                quotaTotal = quota?.required ?: 0,
                entries = entries,
                isLoading = false,
            )
        }
    }
}
