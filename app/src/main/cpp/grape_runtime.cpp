#include "grape_runtime.h"

#include <android/log.h>
#include <android_native_app_glue.h>
#include <dlfcn.h>

#include <string>

namespace {
constexpr const char* TAG = "GrapeRuntime";

void logInfo(const std::string& message) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", message.c_str());
}

void logError(const std::string& message) {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "%s", message.c_str());
}

using RuntimeMain = int (*)(android_app*);

void* g_main_handle = nullptr;
RuntimeMain g_runtime_main = nullptr;
}

namespace grape::runtime {

bool load(const RuntimeConfig& config, std::string& error) {
    unload();
    g_config = config;

    if (config.main_library.empty()) {
        error = "Runtime library path is empty";
        return false;
    }

    logInfo("Loading generic native runtime: " + config.main_library);

    dlerror();
    g_main_handle = dlopen(
        config.main_library.c_str(),
        RTLD_NOW | RTLD_LOCAL
    );

    if (g_main_handle == nullptr) {
        const char* message = dlerror();
        error = message != nullptr ? message : "dlopen failed";
        logError(error);
        return false;
    }

    dlerror();
    void* symbol = dlsym(g_main_handle, "grape_runtime_main");
    const char* symbol_error = dlerror();

    if (symbol == nullptr || symbol_error != nullptr) {
        error = symbol_error != nullptr
            ? symbol_error
            : "grape_runtime_main was not found";
        logError(error);
        unload();
        return false;
    }

    g_runtime_main = reinterpret_cast<RuntimeMain>(symbol);
    logInfo("Generic runtime entry resolved");
    return true;
}

bool runMinecraft(android_app* app, std::string& error) {
    if (app == nullptr) {
        error = "android_app is null";
        return false;
    }

    if (g_runtime_main == nullptr) {
        error = "Generic runtime entry is not loaded";
        return false;
    }

    logInfo("Entering generic native runtime handoff");

    const int result = g_runtime_main(app);
    if (result != 0) {
        error = "Generic runtime returned error code " +
                std::to_string(result);
        logError(error);
        return false;
    }

    logInfo("Generic native runtime handoff finished");
    return true;
}

void unload() {
    g_runtime_main = nullptr;

    if (g_main_handle != nullptr) {
        dlclose(g_main_handle);
        g_main_handle = nullptr;
    }
}

void clearConfig() {
    unload();
    g_config = {};
}

} // namespace grape::runtime
