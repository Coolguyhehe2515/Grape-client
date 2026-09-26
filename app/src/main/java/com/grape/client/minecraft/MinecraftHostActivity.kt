package com.grape.client.minecraft

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast

/**
 * Hosts the Grape native game surface.
 *
 * The Minecraft engine remains the user's official, licensed installation;
 * Grape does not bypass its entitlement or bundle proprietary engine binaries.
 */
class MinecraftHostActivity : Activity(), SurfaceHolder.Callback {
    private lateinit var surfaceView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableColor(Color.BLACK)

        surfaceView = SurfaceView(this).apply {
            holder.addCallback(this@MinecraftHostActivity)
        }
        setContentView(surfaceView)

        val installer = MinecraftRuntimeInstaller(this)
        val installed = installer.install(
            MinecraftInstance(
                instanceId = "default",
                rootDirectory = filesDir.resolve("minecraft/default"),
                nativeDirectory = filesDir.resolve("minecraft/default/libraries/arm64-v8a")
            )
        )

        val runtime = installed.getOrElse {
            Toast.makeText(this, it.message ?: "Minecraft runtime is not ready.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        MinecraftRuntimeValidator.validate(runtime).onFailure {
            Toast.makeText(this, it.message ?: "Minecraft runtime validation failed.", Toast.LENGTH_LONG).show()
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

    override fun surfaceCreated(holder: SurfaceHolder) {
        nativeAttach(holder.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        nativeAttach(holder.surface)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        nativeDetach()
    }

    override fun onDestroy() {
        nativeDetach()
        MinecraftLaunchSession.clear()
        super.onDestroy()
    }

    private external fun nativeAttach(surface: Surface)
    private external fun nativeDetach()

    companion object {
        init {
            System.loadLibrary("grape")
        }
    }
}
