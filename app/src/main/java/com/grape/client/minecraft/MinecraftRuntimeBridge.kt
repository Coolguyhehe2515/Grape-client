package com.grape.client.minecraft

import android.content.Context

/**
 * Safe boundary between the launcher UI and the installed Minecraft runtime.
 * It only validates/discovers the official package; it does not bypass licensing
 * or inject/replace Minecraft native code.
 */
class MinecraftRuntimeBridge(private val context: Context) {
    fun prepare(instance: MinecraftInstance): Result<MinecraftRuntime> {
        return MinecraftRuntimeInstaller(context).install(instance)
    }

    fun describe(runtime: MinecraftRuntime): String {
        return "${runtime.packageName}: ${runtime.nativeLibraryDir.absolutePath}"
    }
}
