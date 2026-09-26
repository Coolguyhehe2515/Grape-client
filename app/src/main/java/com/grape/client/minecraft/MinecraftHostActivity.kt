package com.grape.client.minecraft

import android.os.Bundle
import android.widget.Toast
import com.google.androidgamesdk.GameActivity

/** Native GameActivity host for the Grape Minecraft runtime. */
class MinecraftHostActivity : GameActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val installer = MinecraftRuntimeInstaller(this)
        val instance = MinecraftInstance(
            id = "default",
            packageRoot = filesDir.resolve("minecraft/default"),
            nativeDirectory = filesDir.resolve("minecraft/default/libraries/arm64-v8a")
        )

        val installed = installer.install(instance)
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
            instance.copy(nativeDirectory = runtime.nativeLibraryDir)
        )
    }

    override fun onDestroy() {
        MinecraftLaunchSession.clear()
        super.onDestroy()
    }
}
