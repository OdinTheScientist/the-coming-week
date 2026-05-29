package com.thecomingweek.domain.usecase

import com.thecomingweek.data.repository.PlayerStateRepository
import com.thecomingweek.data.repository.StatRepository
import com.thecomingweek.data.repository.WeekRepository
import com.thecomingweek.domain.model.StatType
import com.thecomingweek.domain.model.Week
import com.thecomingweek.domain.usecase.internal.quotasForTheme
import javax.inject.Inject

class AdvanceWeekUseCase @Inject constructor(
    private val weekRepository: WeekRepository,
    private val statRepository: StatRepository,
    private val playerStateRepository: PlayerStateRepository,
) {

    // Turns the current week into the next. MVP trigger is manual (a ritual act
    // on the Week screen) — no automatic Sunday detection yet.
    suspend operator fun invoke() {
        val current = weekRepository.current() ?: return

        // Stage 10 owns the biome boundary: when weekNumber reaches the biome's
        // weekCount the run should reset (roguelite). Until then weeks keep
        // turning within the same biome — weekNumber simply climbs, which is
        // harmless. No biome lookup happens here, so nothing can crash on it.
        //
        // The next week starts the day after this one ends. Advancing before the
        // week truly ends therefore dates the new week in the future, causing a
        // bounded quota-credit lag (stats/XP/buffs are unaffected). Sunday-aligned
        // advancement is the fix — see docs/roadmap.md "Deferred / post-MVP".
        // Quotas are regenerated from the new theme (not carried over), so the
        // week's demand structure always follows the stat it bends toward.
        val theme = nextTheme(current.statTheme)
        val next = Week(
            id = current.id + 1,
            weekNumber = current.weekNumber + 1,
            statTheme = theme,
            biomeId = current.biomeId,
            startEpochDay = current.endEpochDay + 1,
            endEpochDay = current.endEpochDay + 7,
            quotas = quotasForTheme(theme),
            isResolved = false,
        )

        weekRepository.markResolved(current.id)
        weekRepository.upsert(next)
        statRepository.resetWeeklyGains()
        playerStateRepository.setCurrentWeek(next.id)
    }

    // Themes rotate through StatType in declaration order, wrapping around.
    private fun nextTheme(current: StatType): StatType {
        val all = StatType.entries
        return all[(current.ordinal + 1) % all.size]
    }
}
