# Grape Client — Manual Runtime Scaffold

This folder contains a manual runtime scaffold inspired by the lifecycle used
by Android Minecraft launchers.

Flow:

Grape UI
  -> MinecraftRuntimePreparer
  -> GamePackageManager
  -> prepared instance directory
  -> MinecraftHostActivity
  -> GameActivity
  -> native lifecycle

Important:

`RuntimeLibraryLoader` only loads libraries that are already present in the
Grape-owned runtime directory. It does not copy, patch, decrypt, bypass, or
remove Minecraft licensing/protection components.

`grape_runtime.cpp` is intentionally kept as a generic native loader. Before
using it for any third-party runtime, verify that the runtime and its native
dependencies are authorized to be loaded by the host application.

Copy these files into the corresponding paths in Grape-client and integrate
the activity in AndroidManifest.xml.
