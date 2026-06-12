package com.thecomingweek.domain.usecase.internal

import com.thecomingweek.domain.model.StatType

// MVP enemy roster: one enemy per stat, reused at scaled difficulty across
// weeks and biomes (see docs/design.md "MVP scope"). The week's stat theme
// picks which enemy the daily battle is fought against.
internal fun enemyNameForTheme(theme: StatType): String = when (theme) {
    StatType.STRENGTH -> "The Hollow Grunt"
    StatType.AGILITY -> "The Flinching Shade"
    StatType.VITALITY -> "The Rot-Bloated"
    StatType.INTELLECT -> "The Pale Scribe"
    StatType.CREATIVITY -> "The Withered Muse"
    StatType.WILLPOWER -> "The Kneeling Penitent"
}
