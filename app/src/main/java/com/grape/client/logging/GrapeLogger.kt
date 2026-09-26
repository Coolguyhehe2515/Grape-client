package com.grape.client.logging

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GrapeLogger {
    private val lock = Any()
    private var logFile: File? = null

    fun initialize(context: Context) {
        synchronized(lock) {
            val mediaRoot = context.getExternalMediaDirs().firstOrNull()
            val root = mediaRoot ?: context.getExternalFilesDir(null) ?: context.filesDir
            val logs = File(root, "logs")
            logs.mkdirs()
            logFile = File(logs, "grape.log")
            write("INFO", "GrapeLogger initialized")
        }
    }

    fun info(message: String) = write("INFO", message)
    fun warn(message: String) = write("WARN", message)
    fun error(message: String, throwable: Throwable? = null) {
        val details = if (throwable == null) message else "$message: ${throwable.stackTraceToString()}"
        write("ERROR", details)
    }

    private fun write(level: String, message: String) {
        synchronized(lock) {
            val file = logFile ?: return
            val timestamp = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSSZ",
                Locale.US
            ).format(Date())

            runCatching {
                file.parentFile?.mkdirs()
                file.appendText("[$timestamp] [$level] $message\n")
            }
        }
    }
}
