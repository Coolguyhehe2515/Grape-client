package com.grape.client.minecraft

import android.os.Bundle
import com.google.androidgamesdk.GameActivity
import com.grape.client.logging.GrapeLogger

/**
 * In-process GameActivity host.
 *
 * Native Minecraft code must only be entered after the runtime preparation
 * step succeeds. This activity does not launch Minecraft as a separate app.
 */
class MinecraftHostActivity : GameActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        GrapeLogger.info("MinecraftHostActivity starting")

        val preparer = MinecraftRuntimePreparer(this)
        val instanceResult = preparer.prepare(DEFAULT_INSTANCE_ID)

        if (instanceResult.isFailure) {
            GrapeLogger.error(
                "Minecraft runtime preparation failed: " +
                    instanceResult.exceptionOrNull()?.message
            )
            finish()
            return
        }

        val instance = instanceResult.getOrThrow()

        GrapeLogger.info("Entering GameActivity lifecycle")
        GrapeLogger.info("Runtime root: ${instance.root.absolutePath}")

        super.onCreate(savedInstanceState)

        GrapeLogger.info("GameActivity lifecycle entered")
    }

    companion object {
        private const val DEFAULT_INSTANCE_ID = "default"
    }
}
