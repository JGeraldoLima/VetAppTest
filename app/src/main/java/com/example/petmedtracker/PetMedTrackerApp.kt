package com.example.petmedtracker

import android.app.Application
import com.example.petmedtracker.di.dataModule
import com.example.petmedtracker.di.domainModule
import com.example.petmedtracker.di.presentationModule
import com.example.petmedtracker.data.seed.SeedDataLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PetMedTrackerApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val seedDataLoader: SeedDataLoader by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@PetMedTrackerApp)
            modules(dataModule, domainModule, presentationModule)
        }
        applicationScope.launch {
            seedDataLoader.loadIfNeeded()
        }
    }
}
