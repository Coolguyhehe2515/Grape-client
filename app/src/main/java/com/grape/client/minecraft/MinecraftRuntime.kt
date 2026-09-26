package com.grape.client.minecraft

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import java.io.File

/** Describes the installed Minecraft runtime without bundling Minecraft binaries. */
data class MinecraftRuntime(
    val packageName: String,
    val sourceApk: File,
    val nativeLibraryDir: File,
    val abi: String
) {
    fun resolveLibrary(name: String): File {
        val normalized = name.removePrefix("lib").removeSuffix(".so")
        return File(nativeLibraryDir, "lib$normalized.so")
    }
}

object MinecraftRuntimeProvider {
    const val PACKAGE_NAME = "com.mojang.minecraftpe"

    fun findInstalled(context: Context): MinecraftRuntime? {
        val packageInfo = runCatching {
            context.packageManager.getApplicationInfo(PACKAGE_NAME, 0)
        }.getOrNull() ?: return null

        val nativeDir = packageInfo.nativeLibraryDir?.let(::File) ?: return null
        val source = File(packageInfo.sourceDir)
        val abi = Build.SUPPORTED_ABIS.firstOrNull { it == "arm64-v8a" } ?: return null

        if (!source.isFile || !nativeDir.isDirectory) return null
        return MinecraftRuntime(PACKAGE_NAME, source, nativeDir, abi)
    }
}
