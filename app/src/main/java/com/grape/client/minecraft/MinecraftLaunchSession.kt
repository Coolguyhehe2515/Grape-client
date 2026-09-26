package com.grape.client.minecraft

object MinecraftLaunchSession {
    @Volatile
    private var instance: MinecraftInstance? = null

    fun set(instance: MinecraftInstance) {
        this.instance = instance
    }

    fun get(): MinecraftInstance? = instance

    fun clear() {
        instance = null
    }
}
