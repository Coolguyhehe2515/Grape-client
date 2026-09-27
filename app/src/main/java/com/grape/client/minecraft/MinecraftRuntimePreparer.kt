package com.grape.client.minecraft

import android.content.Context
import com.grape.client.logging.GrapeLogger

class MinecraftRuntimePreparer(context: Context) {

    private val manager = GamePackageManager(context)

    fun prepare(instanceId: String): Result<MinecraftInstance> {
        if (!manager.isArm64Supported()) {
            GrapeLogger.error("Minecraft runtime requires arm64-v8a")
            return Result.failure(
                UnsupportedOperationException("Grape Client requires arm64-v8a")
            )
        }

        val instance = manager.prepareInstance(instanceId)

        GrapeLogger.info("Minecraft runtime prepared: ${instance.root.absolutePath}")
        GrapeLogger.info("Native directory: ${instance.nativeDir.absolutePath}")
        GrapeLogger.info("Data directory: ${instance.dataDir.absolutePath}")

        return Result.success(instance)
    }
}
