package com.grape.client.logging

import android.content.Context

class GrapeCrashHandler private constructor(
    private val previous: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        GrapeLogger.error(
            "Unhandled Java exception on thread ${thread.name}",
            throwable
        )
        previous?.uncaughtException(thread, throwable)
    }

    companion object {
        fun install(context: Context) {
            GrapeLogger.initialize(context)
            val current = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(GrapeCrashHandler(current))
            GrapeLogger.info("Java crash handler installed")
        }
    }
}
