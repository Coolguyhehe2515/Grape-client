package com.grape.client

import android.app.Application
import com.grape.client.logging.GrapeCrashHandler
import com.grape.client.logging.GrapeLogger

class GrapeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        GrapeLogger.initialize(this)
        GrapeLogger.info("Grape Client application started")
        GrapeLogger.info("Package: $packageName")
        GrapeLogger.info("Android SDK: ${android.os.Build.VERSION.SDK_INT}")
        GrapeLogger.info("ABI: ${android.os.Build.SUPPORTED_ABIS.joinToString()}")

        GrapeCrashHandler.install(this)
    }
}
