package com.grape.client.minecraft

import android.content.Context
import java.io.File

/**
 * Prepares a runtime supplied by the user's installed, licensed Minecraft package.
 * Grape never downloads or bundles proprietary Minecraft binaries.
 */
class MinecraftRuntimeInstaller(private val context: Context) {
    fun install(instance: MinecraftInstance): Result<MinecraftRuntime> {
        val runtime = MinecraftRuntimeProvider.findInstalled(context)
            ?: return Result.failure(IllegalStateException("Minecraft for Android is not installed."))

        if (runtime.abi != "arm64-v8a") {
            return Result.failure(UnsupportedOperationException("Only arm64-v8a Minecraft is supported."))
        }

        val target = instance.nativeDirectory
        if (!target.exists() && !target.mkdirs()) {
            return Result.failure(IllegalStateException("Unable to create the runtime directory."))
        }

        File(target, "runtime-source.txt").writeText(
            "source=${runtime.packageName}\nabi=${runtime.abi}\n"
        )

        return Result.success(runtime)
    }
}
