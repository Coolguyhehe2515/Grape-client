package com.grape.client.minecraft

import android.content.Context
import android.content.Intent

/** Launches the user's installed official Minecraft app without replacing its runtime. */
class OfficialMinecraftLauncher(private val context: Context) {
    companion object {
        const val PACKAGE_NAME = "com.mojang.minecraftpe"
    }

    fun launch(): Result<Unit> {
        if (!isInstalled()) {
            return Result.failure(IllegalStateException("Minecraft Bedrock is not installed."))
        }

        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)
            ?: return Result.failure(IllegalStateException("Minecraft launch activity was not found."))

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return Result.success(Unit)
    }

    fun isInstalled(): Boolean = runCatching {
        context.packageManager.getApplicationInfo(PACKAGE_NAME, 0)
    }.isSuccess
}
