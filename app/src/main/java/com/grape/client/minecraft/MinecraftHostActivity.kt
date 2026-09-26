package com.grape.client.minecraft

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast

class MinecraftHostActivity : Activity(), SurfaceHolder.Callback {
    private lateinit var surfaceView: SurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableColor(Color.BLACK)

        surfaceView = SurfaceView(this).apply {
            holder.addCallback(this@MinecraftHostActivity)
        }
        setContentView(surfaceView)

        val prepared = MinecraftRuntimePreparer(this).prepare("default", false)
        if (prepared.isFailure) {
            val message = prepared.exceptionOrNull()?.message ?: "Minecraft runtime is not ready."
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        MinecraftLaunchSession.set(prepared.getOrThrow())
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        NativeHostBridge.attach(holder.surface)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        NativeHostBridge.attach(holder.surface)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        NativeHostBridge.detach()
    }

    override fun onDestroy() {
        NativeHostBridge.detach()
        MinecraftLaunchSession.clear()
        super.onDestroy()
    }
}

private object NativeHostBridge {
    private external fun nativeAttach(surface: android.view.Surface)
    private external fun nativeDetach()

    fun attach(surface: android.view.Surface) {
        runCatching { nativeAttach(surface) }
    }

    fun detach() {
        runCatching { nativeDetach() }
    }
}
