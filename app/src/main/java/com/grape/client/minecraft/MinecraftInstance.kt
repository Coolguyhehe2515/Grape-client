package com.grape.client.minecraft

import java.io.File

data class MinecraftInstance(
    val id: String,
    val root: File,
    val nativeDir: File,
    val dataDir: File
)
