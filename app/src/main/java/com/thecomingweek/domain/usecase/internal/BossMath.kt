package com.thecomingweek.domain.usecase.internal

import com.thecomingweek.domain.model.Boss
import com.thecomingweek.domain.model.Week

// Pure boss/Trial arithmetic, single-sourced here the way computeQuotaProgress
// single-sources the quota rule. Both ResolveWeeklyBossUseCase (which decides
// the Trial) and BossViewModel (which previews it before the player commits)
// call these, so the numbers the player sees are the numbers that judge them.

// The base difficulty every weekly boss starts from before stats, quota
// penalties, and the biome modifier are layered on. A floor that keeps even a
// week-1, empty-stat Trial from being a walkover.
const val BASE_BOSS_DIFFICULTY: Int = 10

// MVP has no authored bosses (see docs design — sprites/bosses are post-MVP),
// so the weekly Trial is a single placeholder Warden built from the current
// week. Its id mirrors the week id: one Trial per week, stable across reads so
// upserting the outcome overwrites cleanly rather than piling up rows.
// post-MVP: authored per-biome bosses with their own names, flavor, sprites.
internal fun placeholderBoss(week: Week): Boss = Boss(
    id = week.id,
    weekId = week.id,
    biomeId = week.biomeId,
    name = "The Warden of the Stone Hours",
    flavor = "It has kept the week. Now it asks what you made of it.",
    baseDifficulty = BASE_BOSS_DIFFICULTY,
    finalDifficulty = 0,
    defeated = null,
)

// The player's standing against the Trial.
//   playerScore = sum of all stat values + 5 per quota met
//
// Permanent stats carry the run's accumulated weight (they persist across the
// roguelite reset), so a player who has trained is measurably stronger every
// week. The flat +5 per met quota is the week's own contribution: it rewards
// answering *this* week's demands, and it is deliberately large enough that a
// fully-met week (6 quotas = +30) decisively outpaces a slacked one even early,
// when stat totals are still small. Quotas thus matter twice over — meeting
// them lifts this score AND removes the quota penalty from the boss's
// difficulty. That double swing is the engine of the week's tension.
internal fun playerScore(statSum: Int, quotasMet: Int): Int =
    statSum + QUOTA_SCORE_BONUS * quotasMet

private const val QUOTA_SCORE_BONUS = 5
