package com.grape.client.minecraft

import android.content.Context
import java.io.File

/**
 * Builds the private runtime layout used by Grape before Minecraft is started.
 * It does not accept arbitrary native libraries from external storage.
 */
class MinecraftRuntimePreparer(private val context: Context) {
    private val manager = GamePackageManager(context)

    fun prepare(instanceId: String, verifiedEntitlement: Boolean): Result<MinecraftInstance> {
        if (!LicenseGate.canLaunch(verifiedEntitlement)) {
            return Result.failure(IllegalStateException("A verified Minecraft license is required."))
        }
        if (!manager.isArm64Supported()) {
            return Result.failure(UnsupportedOperationException("Grape Client requires arm64-v8a."))
        }

        val root = manager.prepareInstance(instanceId)
        val nativeDir = File(root, "libraries/arm64-v8a")
        return Result.success(MinecraftInstance(instanceId, root, nativeDir))
    }
}
