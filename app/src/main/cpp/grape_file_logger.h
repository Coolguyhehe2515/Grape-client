#pragma once

#include <android/log.h>
#include <cerrno>
#include <cstdarg>
#include <cstdio>
#include <cstring>
#include <sys/stat.h>
#include <sys/types.h>

namespace grape::filelog {

inline const char* path() {
    return "/storage/emulated/0/Android/media/com.grape.client/logs/grape.log";
}

inline void ensureDirectory() {
    ::mkdir("/storage/emulated/0/Android/media", 0775);
    ::mkdir("/storage/emulated/0/Android/media/com.grape.client", 0775);
    ::mkdir("/storage/emulated/0/Android/media/com.grape.client/logs", 0775);
}

inline void write(const char* level, const char* format, ...) {
    ensureDirectory();

    FILE* file = std::fopen(path(), "a");
    if (file == nullptr) {
        return;
    }

    std::fprintf(file, "[NATIVE] [%s] ", level);

    va_list args;
    va_start(args, format);
    std::vfprintf(file, format, args);
    va_end(args);

    std::fputc('\n', file);
    std::fflush(file);
    std::fclose(file);
}

} // namespace grape::filelog

#define GRAPE_FILE_LOGI(...) ::grape::filelog::write("INFO", __VA_ARGS__)
#define GRAPE_FILE_LOGE(...) ::grape::filelog::write("ERROR", __VA_ARGS__)
