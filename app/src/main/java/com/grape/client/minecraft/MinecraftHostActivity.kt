package com.grape.client.minecraft

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.grape.client.logging.GrapeLogger

/** Launch bridge for the installed Minecraft Bedrock package. */
class MinecraftHostActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GrapeLogger.info("Minecraft launch bridge started")

        val launchIntent = packageManager.getLaunchIntentForPackage("com.mojang.minecraftpe")
        if (launchIntent == null) {
            GrapeLogger.error("Minecraft launch intent could not be resolved")
            Toast.makeText(
                this,
                "Minecraft Bedrock is not installed or cannot be launched.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        GrapeLogger.info("Launching installed Minecraft package")

        runCatching {
            startActivity(launchIntent)
            GrapeLogger.info("Minecraft launch request sent successfully")
        }.onFailure { throwable ->
            GrapeLogger.error("Minecraft launch request failed", throwable)
            Toast.makeText(
                this,
                "Unable to launch Minecraft.",
                Toast.LENGTH_LONG
            ).show()
        }

        finish()
    }
}
