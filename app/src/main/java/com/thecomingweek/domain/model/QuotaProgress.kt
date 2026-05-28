package com.thecomingweek.domain.model

data class QuotaProgress(
    val stat: StatType,
    val completed: Int,
    val required: Int,
) {
    val met: Boolean get() = completed >= required
}
