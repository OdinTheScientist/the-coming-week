package com.thecomingweek.data.local

import androidx.room.TypeConverter
import com.thecomingweek.domain.model.BuffPolarity
import com.thecomingweek.domain.model.BuffSource
import com.thecomingweek.domain.model.QuestStatus
import com.thecomingweek.domain.model.QuestType
import com.thecomingweek.domain.model.StatType

class Converters {

    @TypeConverter
    fun fromStatType(value: StatType): String = value.name

    @TypeConverter
    fun toStatType(value: String): StatType = StatType.valueOf(value)

    @TypeConverter
    fun fromQuestType(value: QuestType): String = value.name

    @TypeConverter
    fun toQuestType(value: String): QuestType = QuestType.valueOf(value)

    @TypeConverter
    fun fromQuestStatus(value: QuestStatus): String = value.name

    @TypeConverter
    fun toQuestStatus(value: String): QuestStatus = QuestStatus.valueOf(value)

    @TypeConverter
    fun fromBuffPolarity(value: BuffPolarity): String = value.name

    @TypeConverter
    fun toBuffPolarity(value: String): BuffPolarity = BuffPolarity.valueOf(value)

    @TypeConverter
    fun fromBuffSource(value: BuffSource): String = value.name

    @TypeConverter
    fun toBuffSource(value: String): BuffSource = BuffSource.valueOf(value)
}
