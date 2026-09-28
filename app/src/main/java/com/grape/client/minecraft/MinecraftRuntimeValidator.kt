package com.grape.client.minecraft

import java.io.File

object MinecraftRuntimeValidator {
    fun validate(runtime: MinecraftRuntime): Result<Unit> {
        if (runtime.abi != "arm64-v8a") {
            return Result.failure(
                UnsupportedOperationException("Only arm64-v8a is supported.")
            )
        }

        if (!runtime.sourceApk.isFile) {
            return Result.failure(
                IllegalStateException("Minecraft APK was not found.")
            )
        }

        if (!runtime.nativeLibraryDir.isDirectory) {
            return Result.failure(
                IllegalStateException("Minecraft native library directory was not found.")
            )
        }

        return Result.success(Unit)
    }

    fun requireLibrary(runtime: MinecraftRuntime, name: String): Result<File> {
        val file = runtime.resolveLibrary(name)
        return if (file.isFile) {
            Result.success(file)
        } else {
            Result.failure(
                IllegalStateException(
                    "Required native library is missing: ${file.absolutePath}"
                )
            )
        }
    }

    fun dependencyReport(runtime: MinecraftRuntime): List<String> {
        val required = listOf(
            "libminecraftpe.so",
            "libpairipcore.so"
        )

        return required.map { name ->
            val file = runtime.resolveLibrary(name)
            if (file.isFile) {
                "$name=present:${file.absolutePath}"
            } else {
                "$name=missing:${file.absolutePath}"
            }
        }
    }
}
