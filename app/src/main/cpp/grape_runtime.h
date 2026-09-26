#pragma once

#include <string>

struct android_app;

namespace grape::runtime {

struct RuntimeConfig {
    std::string native_library_dir;
    std::string main_library;
};

bool validate(const RuntimeConfig& config, std::string& error);
bool load(const RuntimeConfig& config, std::string& error);
bool runMinecraft(android_app* app, std::string& error);
void unload();
void clearConfig();

}
