package com.grape.client.minecraft

/**
 * Grape intentionally does not support arbitrary user-supplied native modules.
 * Native components must be explicitly declared by the application build.
 */
object NativeModulePolicy {
    private val approvedModules = setOf("grape", "MaterialBinLoader2")

    fun isApproved(moduleName: String): Boolean = moduleName in approvedModules

    fun approvedModules(): Set<String> = approvedModules.toSet()
}
