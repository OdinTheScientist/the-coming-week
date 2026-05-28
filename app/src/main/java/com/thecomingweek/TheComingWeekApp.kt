package com.thecomingweek

import android.app.Application
import com.thecomingweek.data.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class TheComingWeekApp : Application() {

    @Inject lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        // Forces Room to open the DB so the first-launch seed callback fires.
        // Room is lazy and no screen queries the DB yet. TODO(Stage 8): remove
        // once HomeViewModel queries on startup and opens the DB naturally.
        CoroutineScope(Dispatchers.IO).launch {
            database.openHelper.writableDatabase
        }
    }
}
