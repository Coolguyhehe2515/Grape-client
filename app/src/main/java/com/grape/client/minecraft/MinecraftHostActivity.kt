package com.grape.client.minecraft

import android.os.Bundle
import com.google.androidgamesdk.GameActivity
import com.grape.client.logging.GrapeLogger

/**
 * In-process GameActivity host for the Grape native runtime.
 *
 * This does not launch Minecraft as a separate Android application.
 */
class MinecraftHostActivity : GameActivity() {

    private var runtimeConfigured = false

    override fun onCreate(savedInstanceState: Bundle?) {
        GrapeLogger.info("MinecraftHostActivity starting")

        val result = MinecraftRuntimePreparer(this)
            .prepare(DEFAULT_INSTANCE_ID)

        if (result.isFailure) {
            GrapeLogger.error(
                "Runtime preparation failed: " +
                    result.exceptionOrNull()?.message
            )
            finish()
            return
        }

        val instance = result.getOrThrow()

        GrapeLogger.info("Runtime root: ${instance.root.absolutePath}")
        GrapeLogger.info(
            "Runtime native directory: ${instance.nativeDir.absolutePath}"
        )
        GrapeLogger.info("Runtime data directory: ${instance.dataDir.absolutePath}")

        val installedRuntime = MinecraftRuntimeProvider.findInstalled(this)

        if (installedRuntime == null) {
            GrapeLogger.error("Installed Minecraft runtime disappeared during startup")
            finish()
            return
        }

        val mainLibrary = java.io.File(
            installedRuntime.nativeLibraryDir,
            "libminecraftpe.so"
        )

        GrapeLogger.info("Runtime source: ${installedRuntime.sourceApk.absolutePath}")
        GrapeLogger.info("Runtime native source: ${installedRuntime.nativeLibraryDir.absolutePath}")
        GrapeLogger.info("Runtime main library: ${mainLibrary.absolutePath}")

        if (!mainLibrary.isFile) {
            GrapeLogger.error(
                "Minecraft native library was not found: ${mainLibrary.absolutePath}"
            )
            finish()
            return
        }

        nativeConfigureMinecraftRuntime(
            installedRuntime.nativeLibraryDir.absolutePath,
            mainLibrary.absolutePath
        )
        runtimeConfigured = true

        GrapeLogger.info("Starting GameActivity native lifecycle")
        super.onCreate(savedInstanceState)
        GrapeLogger.info("GameActivity native lifecycle started")
    }

    override fun onDestroy() {
        if (runtimeConfigured) {
            nativeClearMinecraftRuntime()
            runtimeConfigured = false
        }

        GrapeLogger.info("MinecraftHostActivity destroyed")
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        GrapeLogger.info("MinecraftHostActivity resumed")
    }

    override fun onPause() {
        GrapeLogger.info("MinecraftHostActivity paused")
        super.onPause()
    }

    private external fun nativeConfigureMinecraftRuntime(
        nativeDirectory: String,
        mainLibrary: String
    )

    private external fun nativeClearMinecraftRuntime()

    companion object {
        private const val DEFAULT_INSTANCE_ID = "default"
    }
}
