package com.grape.client.minecraft

import java.io.File

/** Represents one imported Minecraft game instance managed by Grape. */
data class MinecraftInstance(
    val id: String,
    val packageRoot: File,
    val nativeDirectory: File
) {
    init {
        require(id.matches(Regex("[A-Za-z0-9._-]+"))) { "Invalid instance id" }
    }
}
