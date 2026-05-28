package com.thecomingweek.domain.usecase.internal

import kotlin.random.Random

internal fun <T> weightedDraw(
    items: List<Pair<T, Double>>,
    count: Int,
    random: Random = Random
): List<T> {
    if (items.isEmpty()) return emptyList()
    val n = minOf(count, items.size)
    val remaining = items.toMutableList()
    val result = mutableListOf<T>()
    repeat(n) {
        val totalWeight = remaining.sumOf { it.second }
        if (totalWeight <= 0.0) return result
        var roll = random.nextDouble() * totalWeight
        var selected = remaining.lastIndex
        for (i in remaining.indices) {
            roll -= remaining[i].second
            if (roll <= 0.0) {
                selected = i
                break
            }
        }
        result.add(remaining[selected].first)
        remaining.removeAt(selected)
    }
    return result
}
