package com.grape.client.minecraft

import android.os.Bundle
import com.google.androidgamesdk.GameActivity
import com.grape.client.logging.GrapeLogger

/**
 * In-process Minecraft host based on the GameActivity lifecycle used by
 * Bedrock launchers. It does not start Minecraft as a separate Android app.
 */
class MinecraftHostActivity : GameActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        GrapeLogger.info("Minecraft in-process GameActivity host starting")

        val manager = GamePackageManager(this)
        if (!manager.isArm64Supported()) {
            GrapeLogger.error("Minecraft in-process host requires arm64-v8a")
            finish()
            return
        }

        val instance = manager.prepareInstance(DEFAULT_INSTANCE_ID)
        GrapeLogger.info("Minecraft runtime instance prepared: ${instance.absolutePath}")
        GrapeLogger.info("Entering GameActivity native lifecycle")

        super.onCreate(savedInstanceState)

        GrapeLogger.info("Minecraft GameActivity lifecycle entered")
    }

    companion object {
        private const val DEFAULT_INSTANCE_ID = "default"
    }
}
