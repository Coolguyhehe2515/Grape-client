package com.grape.client.minecraft

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.grape.client.logging.GrapeLogger

/**
 * In-process Minecraft runtime host.
 *
 * The host prepares Grape's private runtime layout first. It deliberately does
 * not start Minecraft as a separate Android application and does not bypass
 * Minecraft's licensing/authentication or native protection mechanisms.
 */
class MinecraftHostActivity : Activity() {

    private lateinit var runtimePreparer: MinecraftRuntimePreparer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runtimePreparer = MinecraftRuntimePreparer(this)

        GrapeLogger.info("Minecraft in-process host started")

        val instanceId = intent.getStringExtra(EXTRA_INSTANCE_ID) ?: DEFAULT_INSTANCE_ID
        GrapeLogger.info("Preparing Minecraft instance: $instanceId")

        val result = runtimePreparer.prepare(
            instanceId = instanceId,
            verifiedEntitlement = intent.getBooleanExtra(EXTRA_VERIFIED_ENTITLEMENT, false)
        )

        result.onSuccess { instance ->
            GrapeLogger.info("Minecraft runtime prepared at: ${instance.root.absolutePath}")
            GrapeLogger.info("Minecraft ARM64 native directory: ${instance.nativeDir.absolutePath}")
            Toast.makeText(
                this,
                "Minecraft runtime prepared.",
                Toast.LENGTH_SHORT
            ).show()
        }.onFailure { throwable ->
            GrapeLogger.error("Minecraft runtime preparation failed", throwable)
            Toast.makeText(
                this,
                throwable.message ?: "Unable to prepare Minecraft runtime.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        const val EXTRA_INSTANCE_ID = "grape.instance_id"
        const val EXTRA_VERIFIED_ENTITLEMENT = "grape.verified_entitlement"
        const val DEFAULT_INSTANCE_ID = "default"
    }
}
