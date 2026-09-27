#include "grape_runtime.h"

#include <dlfcn.h>

namespace {
void* g_main_handle = nullptr;
grape::runtime::RuntimeConfig g_config;
}

namespace grape::runtime {

bool load(const RuntimeConfig& config, std::string& error) {
    unload();
    g_config = config;

    if (config.main_library.empty()) {
        error = "Minecraft main library path is empty";
        return false;
    }

    dlerror();
    g_main_handle = dlopen(config.main_library.c_str(), RTLD_NOW | RTLD_LOCAL);

    if (g_main_handle == nullptr) {
        const char* message = dlerror();
        error = message != nullptr ? message : "dlopen failed";
        return false;
    }

    return true;
}

bool runMinecraft(android_app*, std::string& error) {
    error = "Minecraft native entry is not implemented in this runtime scaffold";
    return false;
}

void unload() {
    if (g_main_handle != nullptr) {
        dlclose(g_main_handle);
        g_main_handle = nullptr;
    }
}

void clearConfig() {
    g_config = {};
}

} // namespace grape::runtime
