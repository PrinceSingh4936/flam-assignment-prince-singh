cmake_minimum_required(VERSION 3.10.2)
project(native-lib)

# Adjust OpenCV path as needed
set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/../OpenCV-android-sdk/sdk/native/jni)
find_package(OpenCV REQUIRED)
include_directories(${OpenCV_INCLUDE_DIRS})

add_library(native-lib SHARED native_processor.cpp)
find_library(log-lib log)
target_link_libraries(native-lib ${OpenCV_LIBS} ${log-lib})
