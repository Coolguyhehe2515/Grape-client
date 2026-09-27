#pragma once

#include <string>

namespace grape::runtime {

struct RuntimeConfig {
    std::string native_library_dir;
    std::string main_library;
};

bool load(const RuntimeConfig& config, std::string& error);
void unload();
void clearConfig();

} // namespace grape::runtime
