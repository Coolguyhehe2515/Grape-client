#include "grape_host.h"

#include <android/log.h>
#include <mutex>

namespace {
std::mutex g_mutex;
ANativeWindow* g_window = nullptr;
}

extern "C" void grape_host_set_surface(ANativeWindow* window) {
    std::lock_guard<std::mutex> lock(g_mutex);

    if (g_window == window) {
        return;
    }

    if (g_window != nullptr) {
        ANativeWindow_release(g_window);
        g_window = nullptr;
    }

    if (window != nullptr) {
        ANativeWindow_acquire(window);
        g_window = window;
        __android_log_print(ANDROID_LOG_INFO, "GrapeHost", "Native surface attached");
    }
}

extern "C" void grape_host_clear_surface() {
    std::lock_guard<std::mutex> lock(g_mutex);

    if (g_window != nullptr) {
        ANativeWindow_release(g_window);
        g_window = nullptr;
    }

    __android_log_print(ANDROID_LOG_INFO, "GrapeHost", "Native surface detached");
}
