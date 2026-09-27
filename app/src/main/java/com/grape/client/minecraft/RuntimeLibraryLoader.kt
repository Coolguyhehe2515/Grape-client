package com.grape.client.minecraft

import com.grape.client.logging.GrapeLogger
import java.io.File

/**
 * Loads only libraries owned by the Grape runtime directory.
 *
 * This intentionally does not copy, patch, extract, or bypass protected
 * Minecraft binaries. A library is loaded only when it already exists in
 * the prepared runtime directory.
 */
object RuntimeLibraryLoader {

    fun load(instance: MinecraftInstance, libraryNames: List<String>): Result<Unit> {
        if (!instance.nativeDir.exists()) {
            return Result.failure(
                IllegalStateException("Native runtime directory does not exist")
            )
        }

        return runCatching {
            for (name in libraryNames) {
                val file = File(instance.nativeDir, name)

                if (!file.isFile) {
                    throw IllegalStateException(
                        "Required runtime library is missing: ${file.absolutePath}"
                    )
                }

                GrapeLogger.info("Loading runtime library: ${file.name}")
                System.load(file.absolutePath)
            }

            GrapeLogger.info("Runtime libraries loaded successfully")
        }
    }
}
