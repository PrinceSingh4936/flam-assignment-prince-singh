# Build Notes (for evaluators)

This file documents exact build-related choices and where native libraries should live.

## Environment
- Android Studio: Arctic Fox or newer (recommended)
- JDK: 11 or 17
- NDK: 21.4.7075529 (recommended)
- CMake: 3.10+
- OpenCV Android SDK: 4.x

## Where to place OpenCV .so files
After downloading OpenCV Android SDK (e.g. `OpenCV-4.x-android-sdk`), copy the prebuilt native lib to the app jniLibs:

