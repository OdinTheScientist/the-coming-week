package com.thecomingweek.di

import android.content.Context
import androidx.room.Room
import com.thecomingweek.data.local.AppDatabase
import com.thecomingweek.data.local.dao.BiomeDao
import com.thecomingweek.data.local.dao.BossDao
import com.thecomingweek.data.local.dao.BuffDao
import com.thecomingweek.data.local.dao.PlayerStateDao
import com.thecomingweek.data.local.dao.QuestDao
import com.thecomingweek.data.local.dao.StatDao
import com.thecomingweek.data.local.dao.WeekDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "coming_week.db").build()

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
