package com.grape.client.minecraft

/**
 * Launch gate. The actual Minecraft entitlement/authentication provider must
 * supply a verified result before a game instance is started.
 */
object LicenseGate {
    fun canLaunch(verifiedEntitlement: Boolean): Boolean = verifiedEntitlement
}
