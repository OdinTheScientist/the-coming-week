package com.thecomingweek.data.mapper

import com.thecomingweek.data.local.entity.QuestEntity
import com.thecomingweek.domain.model.Quest

fun QuestEntity.toDomain(): Quest = Quest(
    id = id,
    title = title,
    flavor = flavor,
    stat = stat,
    type = type,
    xpReward = xpReward,
    statGain = statGain,
    weight = weight,
    status = status,
    dayAssigned = dayAssigned
)

fun Quest.toEntity(): QuestEntity = QuestEntity(
    id = id,
    title = title,
    flavor = flavor,
    stat = stat,
    type = type,
    xpReward = xpReward,
    statGain = statGain,
    weight = weight,
    status = status,
    dayAssigned = dayAssigned
)
