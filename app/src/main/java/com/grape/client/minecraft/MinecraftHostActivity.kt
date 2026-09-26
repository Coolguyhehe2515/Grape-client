package com.grape.client.minecraft

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
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
            Toast.makeText(
                this,
                prepared.exceptionOrNull()?.message ?: "Minecraft runtime is not ready.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }
        MinecraftLaunchSession.set(prepared.getOrThrow())
    }

    override fun surfaceCreated(holder: SurfaceHolder) = nativeAttach(holder.surface)

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        nativeAttach(holder.surface)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) = nativeDetach()

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
