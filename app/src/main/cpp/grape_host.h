#pragma once

#include <android/native_window.h>

#ifdef __cplusplus
extern "C" {
#endif

void grape_host_set_surface(ANativeWindow* window);
void grape_host_clear_surface();

#ifdef __cplusplus
}
#endif
