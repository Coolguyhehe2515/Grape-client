package com.grape.client.minecraft

import android.content.Context
import com.grape.client.logging.GrapeLogger
import java.io.File

/**
 * Prepares the private runtime layout before the Minecraft host activity starts.
 * Native binaries are not modified or copied from arbitrary external paths.
 */
class MinecraftRuntimePreparer(private val context: Context) {
    private val manager = GamePackageManager(context)

    fun prepare(instanceId: String, verifiedEntitlement: Boolean): Result<MinecraftInstance> {
        if (!LicenseGate.canLaunch(verifiedEntitlement)) {
            GrapeLogger.error("Minecraft launch blocked: verified entitlement is required")
            return Result.failure(IllegalStateException("A verified Minecraft license is required."))
        }

        if (!manager.isArm64Supported()) {
            GrapeLogger.error("Minecraft launch blocked: arm64-v8a is unavailable")
            return Result.failure(UnsupportedOperationException("Grape Client requires arm64-v8a."))
        }

        val root = manager.prepareInstance(instanceId)
        val nativeDir = File(root, "libraries/arm64-v8a")
        GrapeLogger.info("Minecraft runtime instance prepared: $instanceId")
        GrapeLogger.info("Minecraft runtime root: ${root.absolutePath}")
        GrapeLogger.info("Minecraft native runtime directory: ${nativeDir.absolutePath}")

        return Result.success(MinecraftInstance(instanceId, root, nativeDir))
    }
}
