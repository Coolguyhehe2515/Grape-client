#include "grape_runtime.h"

#include <android/native_activity.h>
#include <dlfcn.h>
#include <sys/stat.h>

#include <mutex>

#include <game-activity/native_app_glue/android_native_app_glue.h>

namespace {
std::mutex g_mutex;
void* g_main_handle = nullptr;
grape::runtime::RuntimeConfig g_config;
using MinecraftActivityOnCreate = void (*)(ANativeActivity*, void*, size_t);
}

namespace grape::runtime {

bool validate(const RuntimeConfig& config, std::string& error) {
    struct stat dir{};
    if (config.native_library_dir.empty() ||
        stat(config.native_library_dir.c_str(), &dir) != 0 ||
        !S_ISDIR(dir.st_mode)) {
        error = "Minecraft native library directory is unavailable";
        return false;
    }

    if (config.main_library.empty()) {
        error = "Minecraft main library is not configured";
        return false;
    }

    struct stat lib{};
    if (stat(config.main_library.c_str(), &lib) != 0 || !S_ISREG(lib.st_mode)) {
        error = "Minecraft main library is unavailable";
        return false;
    }

    return true;
}

bool load(const RuntimeConfig& config, std::string& error) {
    std::lock_guard<std::mutex> lock(g_mutex);

    if (!validate(config, error)) {
        return false;
    }

    g_config = config;

    if (g_main_handle != nullptr) {
        return true;
    }

    dlerror();
    g_main_handle = dlopen(config.main_library.c_str(), RTLD_NOW | RTLD_GLOBAL);
    if (g_main_handle == nullptr) {
        const char* detail = dlerror();
        error = detail != nullptr ? detail : "Unable to load Minecraft runtime";
        return false;
    }

    return true;
}

bool runMinecraft(android_app* app, std::string& error) {
    std::lock_guard<std::mutex> lock(g_mutex);

    if (g_main_handle == nullptr) {
        error = "Minecraft runtime is not loaded";
        return false;
    }

    if (app == nullptr || app->activity == nullptr) {
        error = "GameActivity native activity instance is unavailable";
        return false;
    }

    dlerror();
    auto* entry = reinterpret_cast<MinecraftActivityOnCreate>(
        dlsym(g_main_handle, "ANativeActivity_onCreate")
    );
    const char* detail = dlerror();

    if (entry == nullptr || detail != nullptr) {
        error = detail != nullptr
            ? detail
            : "Minecraft runtime does not export ANativeActivity_onCreate";
        return false;
    }

    // Bedrock's native startup is based on the NativeActivity entry point.
    // GameActivity is itself built on the NativeActivity model, so forward
    // the native activity instance instead of trying to invoke android_main
    // as a regular function.
    entry(
        app->activity,
        app->savedState,
        app->savedStateSize
    );
    return true;
}

void unload() {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_main_handle != nullptr) {
        dlclose(g_main_handle);
        g_main_handle = nullptr;
    }
}

void clearConfig() {
    std::lock_guard<std::mutex> lock(g_mutex);
    g_config = {};
}

}
