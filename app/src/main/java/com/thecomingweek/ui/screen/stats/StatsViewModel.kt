package com.thecomingweek.ui.screen.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecomingweek.data.repository.StatRepository
import com.thecomingweek.domain.model.Stat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    statRepository: StatRepository,
) : ViewModel() {

    data class UiState(
        val stats: List<Stat> = emptyList(),
        val isLoading: Boolean = true,
    )

    val state: StateFlow<UiState> =
        statRepository.observeAll()
            .map { stats ->
                UiState(
                    // Canonical StatType order, independent of row insertion order.
                    stats = stats.sortedBy { it.type.ordinal },
                    isLoading = false,
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                UiState(isLoading = true),
            )
}
