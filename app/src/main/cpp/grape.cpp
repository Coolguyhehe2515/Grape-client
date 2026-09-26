#include <android/log.h>
#include <game-activity/native_app_glue/android_native_app_glue.h>
#include <jni.h>

#include "grape_runtime.h"
#include "grape_file_logger.h"

#define LOG_TAG "Grape"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_grape_client_MainActivity_nativeVersion(JNIEnv* env, jobject) {
    return env->NewStringUTF("Grape native core 0.3.0");
}

extern "C" JNIEXPORT void JNICALL
Java_com_grape_client_minecraft_MinecraftHostActivity_nativeConfigureMinecraftRuntime(
    JNIEnv* env,
    jobject,
    jstring nativeDirectory,
    jstring mainLibrary
) {
    const char* nativeDirChars = env->GetStringUTFChars(nativeDirectory, nullptr);
    const char* mainLibraryChars = env->GetStringUTFChars(mainLibrary, nullptr);

    grape::runtime::RuntimeConfig config;
    config.native_library_dir = nativeDirChars != nullptr ? nativeDirChars : "";
    config.main_library = mainLibraryChars != nullptr ? mainLibraryChars : "";

    GRAPE_FILE_LOGI("Preparing Minecraft runtime: %s", config.main_library.c_str());
    GRAPE_FILE_LOGI("Minecraft native directory: %s", config.native_library_dir.c_str());

    std::string error;
    if (!grape::runtime::load(config, error)) {
        LOGE("Minecraft runtime preparation failed: %s", error.c_str());
        GRAPE_FILE_LOGE("Minecraft runtime preparation failed: %s", error.c_str());
    } else {
        LOGI("Minecraft runtime prepared: %s", config.main_library.c_str());
        GRAPE_FILE_LOGI("Minecraft runtime prepared successfully");
    }

    if (nativeDirChars != nullptr) {
        env->ReleaseStringUTFChars(nativeDirectory, nativeDirChars);
    }
    if (mainLibraryChars != nullptr) {
        env->ReleaseStringUTFChars(mainLibrary, mainLibraryChars);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_grape_client_minecraft_MinecraftHostActivity_nativeClearMinecraftRuntime(
    JNIEnv*,
    jobject
) {
    GRAPE_FILE_LOGI("Clearing Minecraft runtime");
    grape::runtime::unload();
    grape::runtime::clearConfig();
}

static void handleAppCommand(android_app* app, int32_t cmd) {
    switch (cmd) {
        case APP_CMD_INIT_WINDOW:
            LOGI("GameActivity window initialized");
            GRAPE_FILE_LOGI("GameActivity window initialized");
            break;
        case APP_CMD_TERM_WINDOW:
            LOGI("GameActivity window terminated");
            GRAPE_FILE_LOGI("GameActivity window terminated");
            break;
        case APP_CMD_GAINED_FOCUS:
            LOGI("GameActivity gained focus");
            GRAPE_FILE_LOGI("GameActivity gained focus");
            break;
        case APP_CMD_LOST_FOCUS:
            LOGI("GameActivity lost focus");
            GRAPE_FILE_LOGI("GameActivity lost focus");
            break;
        default:
            break;
    }
}

extern "C" void android_main(struct android_app* app) {
    app->onAppCmd = handleAppCommand;
    LOGI("Grape GameActivity native entry started");
    GRAPE_FILE_LOGI("Grape GameActivity native entry started");

    std::string error;
    if (grape::runtime::runMinecraft(app, error)) {
        LOGI("Minecraft native entry returned");
        GRAPE_FILE_LOGI("Minecraft native entry returned");
        return;
    }

    LOGE("Minecraft native boot failed: %s", error.c_str());
    GRAPE_FILE_LOGE("Minecraft native boot failed: %s", error.c_str());

    while (true) {
        int events = 0;
        android_poll_source* source = nullptr;

        while (ALooper_pollOnce(app->destroyRequested ? 0 : -1,
                                nullptr,
                                &events,
                                reinterpret_cast<void**>(&source)) >= 0) {
            if (source != nullptr) {
                source->process(app, source);
            }

            if (app->destroyRequested) {
                LOGI("Grape native runtime shutting down");
                GRAPE_FILE_LOGI("Grape native runtime shutting down");
                return;
            }
        }
    }
}
