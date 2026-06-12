package com.thecomingweek.ui.navigation

sealed class Route(val path: String) {
    data object Splash : Route("splash")
    data object Home : Route("home")
    data object Stats : Route("stats")
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
}
