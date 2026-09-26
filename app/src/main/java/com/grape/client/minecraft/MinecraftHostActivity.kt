package com.grape.client.minecraft

import android.os.Bundle
import com.google.androidgamesdk.GameActivity
import android.widget.Toast

/**
 * Native GameActivity host for the Grape Minecraft runtime.
 *
 * The Minecraft engine remains the user's official, licensed installation.
 * Grape does not bundle proprietary Minecraft binaries or bypass entitlement checks.
 */
class MinecraftHostActivity : GameActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val installer = MinecraftRuntimeInstaller(this)
        val installed = installer.install(
            MinecraftInstance(
                instanceId = "default",
                rootDirectory = filesDir.resolve("minecraft/default"),
                nativeDirectory = filesDir.resolve("minecraft/default/libraries/arm64-v8a")
            )
        )

        val runtime = installed.getOrElse {
            Toast.makeText(
                this,
                it.message ?: "Minecraft runtime is not ready.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        MinecraftRuntimeValidator.validate(runtime).onFailure {
            Toast.makeText(
                this,
                it.message ?: "Minecraft runtime validation failed.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        MinecraftLaunchSession.set(
            MinecraftInstance(
                instanceId = "default",
                rootDirectory = filesDir.resolve("minecraft/default"),
                nativeDirectory = runtime.nativeLibraryDir
            )
        )
    }

    override fun onDestroy() {
        MinecraftLaunchSession.clear()
        super.onDestroy()
    }
}
