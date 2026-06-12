package com.thecomingweek.ui.screen.hero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecomingweek.data.repository.BuffRepository
import com.thecomingweek.data.repository.PlayerStateRepository
import com.thecomingweek.data.repository.StatRepository
import com.thecomingweek.domain.model.Buff
import com.thecomingweek.domain.model.Stat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HeroViewModel @Inject constructor(
    playerStateRepository: PlayerStateRepository,
    statRepository: StatRepository,
    buffRepository: BuffRepository,
) : ViewModel() {

    data class UiState(
        val level: Int = 1,
        val xp: Int = 0,
        val xpToNext: Int = 100,
        val currentHp: Int = 0,
        val maxHp: Int = 0,
        val stats: List<Stat> = emptyList(),
        val activeBuffs: List<Buff> = emptyList(),
        val isLoading: Boolean = true,
    )

    private val epochDay: Long = LocalDate.now().toEpochDay()

    val state: StateFlow<UiState> =
        combine(
            playerStateRepository.observe(),
            statRepository.observeAll(),
            buffRepository.observeActive(epochDay),
        ) { playerState, stats, buffs ->
            UiState(
                level = playerState?.level ?: 1,
                xp = playerState?.xp ?: 0,
                xpToNext = (playerState?.level ?: 1) * 100,
                currentHp = playerState?.currentHp ?: 0,
                maxHp = playerState?.maxHp ?: 0,
                stats = stats.sortedBy { it.type.ordinal },
                activeBuffs = buffs,
                isLoading = false,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState(isLoading = true),
        )
}
