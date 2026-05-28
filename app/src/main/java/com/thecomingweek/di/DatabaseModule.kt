package com.thecomingweek.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.thecomingweek.data.local.AppDatabase
import com.thecomingweek.data.local.dao.BiomeDao
import com.thecomingweek.data.local.dao.BossDao
import com.thecomingweek.data.local.dao.BuffDao
import com.thecomingweek.data.local.dao.PlayerStateDao
import com.thecomingweek.data.local.dao.QuestDao
import com.thecomingweek.data.local.dao.StatDao
import com.thecomingweek.data.local.dao.WeekDao
import com.thecomingweek.data.local.seed.Seed
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        lateinit var instance: AppDatabase
        instance = Room.databaseBuilder(context, AppDatabase::class.java, "coming_week.db")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        Seed.populate(instance)
                    }
                }
            })
            .build()
        return instance
    }

    @Provides
    fun provideQuestDao(db: AppDatabase): QuestDao = db.questDao()

    @Provides
    fun provideStatDao(db: AppDatabase): StatDao = db.statDao()

    @Provides
    fun provideWeekDao(db: AppDatabase): WeekDao = db.weekDao()

    @Provides
    fun provideBossDao(db: AppDatabase): BossDao = db.bossDao()

    @Provides
    fun provideBiomeDao(db: AppDatabase): BiomeDao = db.biomeDao()

    @Provides
    fun providePlayerStateDao(db: AppDatabase): PlayerStateDao = db.playerStateDao()

    @Provides
    fun provideBuffDao(db: AppDatabase): BuffDao = db.buffDao()
}
