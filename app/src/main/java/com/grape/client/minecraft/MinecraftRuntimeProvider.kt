package com.grape.client.minecraft

import android.content.Context
import android.content.pm.ApplicationInfo

/** Finds an installed Minecraft package without copying or modifying its binaries. */
object MinecraftRuntimeProvider {
    private const val MINECRAFT_PACKAGE = "com.mojang.minecraftpe"

    fun findInstalled(context: Context): InstalledMinecraftRuntime? {
        val info = try {
            context.packageManager.getApplicationInfo(MINECRAFT_PACKAGE, 0)
        } catch (_: Exception) {
            return null
        }

        val nativeDir = info.nativeLibraryDir ?: return null
        return InstalledMinecraftRuntime(
            packageName = MINECRAFT_PACKAGE,
            sourceDir = info.sourceDir,
            nativeLibraryDir = nativeDir,
            abi = if (nativeDir.contains("arm64")) "arm64-v8a" else "unknown"
        )
    }
}

data class InstalledMinecraftRuntime(
    val packageName: String,
    val sourceDir: String,
    val nativeLibraryDir: String,
    val abi: String
)
