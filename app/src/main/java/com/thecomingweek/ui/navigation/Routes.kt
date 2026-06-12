package com.thecomingweek.ui.navigation

import com.thecomingweek.domain.model.StatType

sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Home : Route("home")
    data object Hero : Route("hero")
    data object Week : Route("week")
    data object Boss : Route("boss")
    data object Battle : Route("battle") {
        const val PATTERN = "battle?force={force}"
        fun path(force: Boolean) = "battle?force=$force"
    }
    data object Biome : Route("biome")
    data class QuestDetail(val id: String) : Route("quest/$id") {
        companion object {
            const val PATTERN = "quest/{id}"
        }
    }
    data class StatQuests(val stat: StatType) : Route("statquests/${stat.name}") {
        companion object {
            const val PATTERN = "statquests/{stat}"
        }
    }
}
