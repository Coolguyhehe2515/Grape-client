package com.grape.client.minecraft

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.widget.Toast
import com.google.androidgamesdk.GameActivity

/** Native GameActivity host for the official Minecraft Bedrock runtime. */
class MinecraftHostActivity : GameActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val applicationInfo = try {
            packageManager.getApplicationInfo("com.mojang.minecraftpe", 0)
        } catch (_: Exception) {
            Toast.makeText(
                this,
                "Minecraft Bedrock is not installed.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        val nativeDir = applicationInfo.nativeLibraryDir
        val mainLibrary = nativeDir?.let { "$it/libminecraftpe.so" }

        if (nativeDir.isNullOrBlank() || mainLibrary.isNullOrBlank()) {
            Toast.makeText(
                this,
                "Minecraft ARM64 runtime was not found.",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        prepareMinecraftAssets(applicationInfo)
        nativeConfigureMinecraftRuntime(nativeDir, mainLibrary)

        super.onCreate(savedInstanceState)
    }

    private fun prepareMinecraftAssets(info: ApplicationInfo) {
        runCatching {
            val assets = assets
            val addAssetPath = assets.javaClass.getMethod(
                "addAssetPath",
                String::class.java
            )
            addAssetPath.invoke(assets, info.sourceDir)
            info.splitSourceDirs?.forEach { split ->
                addAssetPath.invoke(assets, split)
            }
        }
    }

    private external fun nativeConfigureMinecraftRuntime(
        nativeDirectory: String,
        mainLibrary: String
    )

    override fun onDestroy() {
        nativeClearMinecraftRuntime()
        super.onDestroy()
    }

    private external fun nativeClearMinecraftRuntime()
}
