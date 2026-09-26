package com.grape.client.storage

import android.content.Context
import java.io.File

/**
 * External app-specific storage for files that should be accessible through
 * tools such as Shizuku without requiring root access.
 *
 * Location:
 * /storage/emulated/0/Android/data/com.grape.client/files/
 */
object GrapeStorage {
    fun root(context: Context): File {
        return requireNotNull(context.getExternalFilesDir(null)) {
            "External storage is unavailable."
        }.also { it.mkdirs() }
    }

    fun runtime(context: Context): File = directory(context, "runtime")
    fun logs(context: Context): File = directory(context, "logs")
    fun config(context: Context): File = directory(context, "config")
    fun cache(context: Context): File = directory(context, "cache")

    private fun directory(context: Context, name: String): File {
        return File(root(context), name).also { it.mkdirs() }
    }
}
