package com.grape.client.minecraft

/**
 * Immutable launch description. Grape deliberately does not expose a
 * user-controlled native-library path.
 */
data class MinecraftLaunchPlan(
    val instance: MinecraftInstance,
    val arm64Only: Boolean = true
)
