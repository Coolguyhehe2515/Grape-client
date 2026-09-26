cmake_minimum_required(VERSION 3.22.1)
project(grape)

add_library(grape SHARED
    grape.cpp
    grape_host.cpp
    grape_runtime.cpp
)

target_compile_features(grape PRIVATE cxx_std_17)

target_include_directories(grape PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

find_library(log-lib log)
find_library(android-lib android)

find_package(game-activity REQUIRED CONFIG)

target_link_libraries(grape
    game-activity::game-activity_static
    ${log-lib}
    ${android-lib}
    dl
)
