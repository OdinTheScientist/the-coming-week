package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.BiomeRepository
import com.thecomingweek.data.repository.PlayerStateRepository
import com.thecomingweek.data.repository.StatRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.Biome
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.domain.model.Week
import com.thecomingweek.domain.usecase.internal.quotasForTheme
import javax.inject.Inject

// The roguelite reset, run when a biome's final Trial passes. Its own class
// (per docs/architecture.md) so the boss's job ends at "the biome is over" and
// the consequences of that live here.
//
// What resets: the biome (a new descent begins), the week (back to Week 1),
// weekly stat gains. What PERSISTS: permanent stat values and the run counter
// (which climbs). That persistence IS the roguelite mechanic — you carry your
// trained strength into the next descent; you do not carry the week's buffs.
class ResetRunUseCase @Inject constructor(
    private val biomeRepository: BiomeRepository,
    private val weekRepository: WeekRepository,
    private val statRepository: StatRepository,
    private val playerStateRepository: PlayerStateRepository,
) {

    suspend operator fun invoke() {
        val biome = biomeRepository.current() ?: return
        val finalWeek = weekRepository.current() ?: return

        // Close out the descent that just ended.
        biomeRepository.markCompleted(biome.id)
        weekRepository.markResolved(finalWeek.id)

        // The new descent begins the day after the old biome's last week.
        val newStart = finalWeek.endEpochDay + 1
        val newBiomeId = biome.id + 1

        // MVP authors a single biome ("The Stone Hours"), so the new descent is
        // another instance of it. post-MVP: rotate through multiple biomes here.
        biomeRepository.upsert(
            Biome(
                id = newBiomeId,
                name = biome.name,
                flavor = biome.flavor,
                weekCount = biome.weekCount,
                startEpochDay = newStart,
                finalBossId = null,
                isCompleted = false,
            )
        )

        // Week 1 of the new biome. Theme returns to the start of the rotation —
        // a new descent begins where the cycle begins. Quotas come from the
        // theme (same rule as Seed and AdvanceWeekUseCase), not the old week.
        val theme = StatType.entries.first()
        val newWeek = Week(
            id = finalWeek.id + 1,
            weekNumber = 1,
            statTheme = theme,
            biomeId = newBiomeId,
            startEpochDay = newStart,
            endEpochDay = newStart + 6,
            quotas = quotasForTheme(theme),
            isResolved = false,
        )
        weekRepository.upsert(newWeek)

        // Weekly gains reset every week boundary; the biome boundary is one too.
        // Stat *values* are deliberately left alone — the persistence is the point.
        statRepository.resetWeeklyGains()

        // runNumber++ and re-point the player at the new biome's first week.
        playerStateRepository.startNewRun(newBiomeId, newWeek.id)
    }
}
