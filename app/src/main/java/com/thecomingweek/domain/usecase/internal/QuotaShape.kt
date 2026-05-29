package com.thecomingweek.domain.usecase.internal

import com.thecomingweek.domain.model.StatType

// Single source of the weekly quota SHAPE, shared by Seed (Week 1) and
// AdvanceWeekUseCase / ResetRunUseCase (every subsequent week). Tying the shape
// to the theme here is what keeps the demand structure following the theme
// instead of drifting from it — the bug this fixes was a week advancing its
// theme while carrying the previous week's quotas forward unchanged.
//
// The shape, given a theme:
//   - the theme stat       → 3   (the week bends toward it)
//   - the next two stats    → 2   (StatType declaration order, wrapping)
//   - the remaining three    → 1
// Total demand = 3 + 2 + 2 + 1 + 1 + 1 = 10, constant every week.
//
// "The next two" are theme.ordinal + 1 and + 2 (mod 6). Because the theme
// itself advances one step in that same order each week, the secondary-2 stats
// rotate alongside it, so consecutive weeks emphasise different stats and feel
// distinct rather than repeating one fixed pattern.
internal fun quotasForTheme(theme: StatType): Map<StatType, Int> {
    val all = StatType.entries
    val size = all.size
    val secondaryA = all[(theme.ordinal + 1) % size]
    val secondaryB = all[(theme.ordinal + 2) % size]
    return all.associateWith { stat ->
        when (stat) {
            theme -> 3
            secondaryA, secondaryB -> 2
            else -> 1
        }
    }
}
