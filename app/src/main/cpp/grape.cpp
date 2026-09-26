#include <android/log.h>
#include <game-activity/native_app_glue/android_native_app_glue.h>
#include <jni.h>

#define LOG_TAG "Grape"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_grape_client_MainActivity_nativeVersion(JNIEnv* env, jobject) {
    return env->NewStringUTF("Grape native core 0.2.0");
}

static void handleAppCommand(android_app* app, int32_t cmd) {
    switch (cmd) {
        case APP_CMD_INIT_WINDOW:
            LOGI("GameActivity window initialized");
            break;
        case APP_CMD_TERM_WINDOW:
            LOGI("GameActivity window terminated");
            break;
        case APP_CMD_GAINED_FOCUS:
            LOGI("GameActivity gained focus");
            break;
        case APP_CMD_LOST_FOCUS:
            LOGI("GameActivity lost focus");
            break;
        default:
            break;
    }
}

extern "C" void android_main(struct android_app* app) {
    app->onAppCmd = handleAppCommand;
    LOGI("Grape GameActivity native entry started");

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
                return;
            }
        }
    }
}
