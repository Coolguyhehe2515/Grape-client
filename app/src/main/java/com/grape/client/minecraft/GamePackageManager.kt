package com.grape.client.minecraft

import android.content.Context
import java.io.File

/**
 * ARM64-only game package manager inspired by the architecture used by
 * Android Minecraft launchers. It prepares an imported game package without
 * attempting to bypass Minecraft's own licensing or authentication.
 */
class GamePackageManager(private val context: Context) {

    private val root: File
        get() = File(context.filesDir, "minecraft")

    val instancesDir: File
        get() = File(root, "instances")

    fun instanceDir(id: String): File {
        require(id.matches(Regex("[A-Za-z0-9._-]+"))) { "Invalid instance id" }
        return File(instancesDir, id)
    }

    fun prepareInstance(id: String): File {
        val dir = instanceDir(id)
        File(dir, "libraries/arm64-v8a").mkdirs()
        File(dir, "data").mkdirs()
        return dir
    }

    fun isArm64Supported(): Boolean = android.os.Build.SUPPORTED_ABIS.any {
        it == "arm64-v8a"
    }
}
