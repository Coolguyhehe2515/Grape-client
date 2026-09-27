package com.grape.client.minecraft

import android.content.Context
import android.os.Build
import java.io.File

class GamePackageManager(private val context: Context) {

    private val root: File
        get() = File(context.filesDir, "minecraft")

    fun instanceDir(id: String): File {
        require(id.matches(Regex("[A-Za-z0-9._-]+"))) {
            "Invalid Minecraft instance id"
        }
        return File(root, "instances/$id")
    }

    fun prepareInstance(id: String): MinecraftInstance {
        val instance = instanceDir(id)
        val nativeDir = File(instance, "libraries/arm64-v8a")
        val dataDir = File(instance, "data")

        nativeDir.mkdirs()
        dataDir.mkdirs()

        return MinecraftInstance(
            id = id,
            root = instance,
            nativeDir = nativeDir,
            dataDir = dataDir
        )
    }

    fun isArm64Supported(): Boolean =
        Build.SUPPORTED_ABIS.any { it == "arm64-v8a" }
}
