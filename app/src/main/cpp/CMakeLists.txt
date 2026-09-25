cmake_minimum_required(VERSION 3.22.1)
project(grape)

add_library(grape SHARED grape.cpp)

target_compile_features(grape PRIVATE cxx_std_17)

find_library(log-lib log)
target_link_libraries(grape ${log-lib})
