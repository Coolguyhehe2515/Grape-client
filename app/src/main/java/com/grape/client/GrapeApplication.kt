package com.grape.client

import android.app.Application
import com.grape.client.logging.GrapeCrashHandler
import com.grape.client.logging.GrapeLogger

class GrapeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GrapeLogger.initialize(this)
        GrapeLogger.info("Grape Client startup")
        GrapeCrashHandler.install(this)
    }
}
