package com.thecomingweek.domain.model

// The outcome of a weekly Trial. Carries the two numbers that decided it
// (so the screen can show the player exactly what was weighed) and, on a loss,
// the debuff that was granted. Pure domain — no storage, no Android.
data class TrialResult(
    val defeated: Boolean,
    val finalDifficulty: Int,
    val playerScore: Int,
    val debuffGranted: Buff?,
)
