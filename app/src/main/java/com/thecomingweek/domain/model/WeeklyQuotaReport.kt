package com.thecomingweek.domain.model

data class WeeklyQuotaReport(
    val progress: List<QuotaProgress>,
) {
    val allMet: Boolean get() = progress.all { it.met }
}
