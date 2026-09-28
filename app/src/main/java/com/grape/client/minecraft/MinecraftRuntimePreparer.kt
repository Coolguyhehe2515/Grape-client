package com.grape.client.minecraft

import android.content.Context
import com.grape.client.logging.GrapeLogger

class MinecraftRuntimePreparer(private val context: Context) {

    private val manager = GamePackageManager(context)
    private val installer = MinecraftRuntimeInstaller(context)

    fun prepare(instanceId: String): Result<MinecraftInstance> {
        if (!manager.isArm64Supported()) {
            GrapeLogger.error("Minecraft runtime requires arm64-v8a")
            return Result.failure(
                UnsupportedOperationException("Grape Client requires arm64-v8a")
            )
        }

        val runtime = MinecraftRuntimeProvider.findInstalled(context)
            ?: return Result.failure(
                IllegalStateException("Minecraft for Android is not installed.")
            )

        MinecraftRuntimeValidator.validate(runtime).getOrElse { error ->
            GrapeLogger.error("Minecraft runtime validation failed: ${error.message}")
            return Result.failure(error)
        }

        GrapeLogger.info("Minecraft package: ${runtime.packageName}")
        GrapeLogger.info("Minecraft APK: ${runtime.sourceApk.absolutePath}")
        GrapeLogger.info("Minecraft native directory: ${runtime.nativeLibraryDir.absolutePath}")

        for (entry in MinecraftRuntimeValidator.dependencyReport(runtime)) {
            GrapeLogger.info("Runtime dependency: $entry")
        }

        val instance = manager.prepareInstance(instanceId)

        installer.install(instance).getOrElse { error ->
            GrapeLogger.error("Minecraft runtime installation failed: ${error.message}")
            return Result.failure(error)
        }

        GrapeLogger.info("Minecraft runtime prepared: ${instance.root.absolutePath}")
        GrapeLogger.info("Native directory: ${instance.nativeDir.absolutePath}")
        GrapeLogger.info("Data directory: ${instance.dataDir.absolutePath}")

        return Result.success(instance)
    }
}
