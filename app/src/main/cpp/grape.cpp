#include <jni.h>
#include <android/native_window_jni.h>
#include "grape_host.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_grape_client_MainActivity_nativeVersion(JNIEnv* env, jobject) {
    return env->NewStringUTF("Grape native core 0.1.0");
}

extern "C" JNIEXPORT void JNICALL
Java_com_grape_client_MainActivity_nativeAttachSurface(JNIEnv* env, jobject, jobject surface) {
    if (surface == nullptr) {
        grape_host_clear_surface();
        return;
    }

    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    if (window == nullptr) {
        grape_host_clear_surface();
        return;
    }

    grape_host_set_surface(window);
    ANativeWindow_release(window);
}

extern "C" JNIEXPORT void JNICALL
Java_com_grape_client_MainActivity_nativeDetachSurface(JNIEnv*, jobject) {
    grape_host_clear_surface();
}
