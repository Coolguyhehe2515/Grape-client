package com.grape.client.minecraft

import android.content.Context

class MinecraftLauncher(private val context: Context) {
    private val packages = GamePackageManager(context)

    fun prepare(instanceId: String, verifiedEntitlement: Boolean): Result<String> {
        if (!LicenseGate.canLaunch(verifiedEntitlement)) {
            return Result.failure(IllegalStateException("A verified Minecraft license is required."))
        }
        if (!packages.isArm64Supported()) {
            return Result.failure(UnsupportedOperationException("Grape Client requires arm64-v8a."))
        }
        val instance = packages.prepareInstance(instanceId)
        return Result.success(instance.absolutePath)
    }
}
