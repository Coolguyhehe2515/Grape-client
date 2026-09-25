#include <jni.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_grape_client_MainActivity_nativeVersion(JNIEnv* env, jobject) {
    return env->NewStringUTF("Grape native core 0.1.0");
}
