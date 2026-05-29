package com.thecomingweek.ui.screen.biome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecomingweek.data.repository.BiomeRepository
import com.thecomingweek.data.repository.PlayerStateRepository
import com.thecomingweek.data.repository.WeekRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BiomeViewModel @Inject constructor(
    biomeRepository: BiomeRepository,
    weekRepository: WeekRepository,
    playerStateRepository: PlayerStateRepository,
) : ViewModel() {

    data class UiState(
        val name: String = "",
        val flavor: String = "",
        val weekNumber: Int = 0,
        val weekCount: Int = 0,
        val runNumber: Int = 1,
        val hasBiome: Boolean = false,
        val isLoading: Boolean = true,
    )

    // Reactive on all three so the screen keeps step with the loop: the week
    // turning bumps weekNumber, and a run reset swaps the biome and increments
    // the run. The run number comes from PlayerState, the descent's progress
    // from the current week, and the descent's identity from the current biome.
    val state: StateFlow<UiState> =
        combine(
            biomeRepository.observeCurrent(),
            weekRepository.observeCurrent(),
            playerStateRepository.observe(),
        ) { biome, week, player ->
            if (biome == null) {
                UiState(hasBiome = false, isLoading = false)
            } else {
                UiState(
                    name = biome.name,
                    flavor = biome.flavor,
                    weekNumber = week?.weekNumber ?: 0,
                    weekCount = biome.weekCount,
                    runNumber = player?.runNumber ?: 1,
                    hasBiome = true,
                    isLoading = false,
                )
            }
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                UiState(isLoading = true),
            )
}
