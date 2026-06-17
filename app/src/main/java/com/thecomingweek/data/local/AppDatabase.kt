package com.thecomingweek.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thecomingweek.data.local.dao.BattleResultDao
import com.thecomingweek.data.local.dao.BiomeDao
import com.thecomingweek.data.local.dao.BossDao
import com.thecomingweek.data.local.dao.BuffDao
import com.thecomingweek.data.local.dao.DayRecordDao
import com.thecomingweek.data.local.dao.PlayerStateDao
import com.thecomingweek.data.local.dao.QuestDao
import com.thecomingweek.data.local.dao.StatDao
import com.thecomingweek.data.local.dao.WeekDao
import com.thecomingweek.data.local.entity.BattleResultEntity
import com.thecomingweek.data.local.entity.BiomeEntity
import com.thecomingweek.data.local.entity.BossEntity
import com.thecomingweek.data.local.entity.BuffEntity
import com.thecomingweek.data.local.entity.DayRecordEntity
import com.thecomingweek.data.local.entity.PlayerStateEntity
import com.thecomingweek.data.local.entity.QuestEntity
import com.thecomingweek.data.local.entity.StatEntity
import com.thecomingweek.data.local.entity.WeekEntity

@Database(
    entities = [
        QuestEntity::class,
        StatEntity::class,
        WeekEntity::class,
        BossEntity::class,
        BiomeEntity::class,
        PlayerStateEntity::class,
        BuffEntity::class,
        BattleResultEntity::class,
        DayRecordEntity::class,
    ],
    version = 7,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun statDao(): StatDao
    abstract fun weekDao(): WeekDao
    abstract fun bossDao(): BossDao
    abstract fun biomeDao(): BiomeDao
    abstract fun playerStateDao(): PlayerStateDao
    abstract fun buffDao(): BuffDao
    abstract fun battleResultDao(): BattleResultDao
    abstract fun dayRecordDao(): DayRecordDao
}
