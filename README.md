# Flam Assignment — Prince Singh

**Author:** Prince Singh  
**Email:** prince.singh.de@gmail.com  
**GitHub:** https://github.com/PrinceSingh4936

---

## Project Summary
A minimal real-time edge detection viewer showing integration between Android Camera2 → JNI (OpenCV C++) processing → rendering using OpenGL ES 2.0. A small TypeScript web viewer shows a saved processed frame with overlayed FPS/resolution stats.

This implementation focuses on correct integration (JNI, OpenCV, OpenGL ES) and a buildable, modular repo that meets the assessment brief.

---

## Key Features (Must-Have implemented)
1. Camera feed (Camera2 + TextureView) streaming frames to native code.
2. Frame processing in C++ (OpenCV): grayscale -> GaussianBlur -> Canny edge detection.
3. Rendering processed frames using OpenGL ES 2.0 as textures (GLSurfaceView).
4. Toggle between raw camera feed and processed edge output.
5. FPS counter on Android.
6. TypeScript web viewer (static processed frame + FPS/resolution overlay).
7. Modular structure: `/app`, `/jni`, `/web`.

---

## How this works (Architecture)
- Android `Camera2Controller` captures YUV frames from the camera (YUV_420_888) and converts to NV21 (helper `YuvUtils`).
- Frames are sent to native C++ (`processFrameNV21`) via JNI.
- Native C++ uses OpenCV to convert NV21 to `cv::Mat`, apply Canny, convert to RGBA (uchar* buffer), and return the RGBA byte array.
- The `GLRenderer` receives the RGBA buffer and updates an OpenGL texture (GL_RGBA / GL_UNSIGNED_BYTE) then draws the fullscreen textured quad for smooth rendering.
- The web viewer (`/web`) accepts a saved processed frame (base64) and displays overlayed FPS/resolution info (to demonstrate bridging native results to a web UI).

---

## Setup instructions

### Prereqs
- Android Studio (Arctic Fox or later)
- NDK (recommended 21.x)
- CMake
- OpenCV Android SDK (4.x)
- Node >= 16 for web

### Android (build & run)
1. Open `/app` in Android Studio.
2. Put the OpenCV `libopencv_java4.so` into `app/src/main/jniLibs/<abi>/` (e.g. `arm64-v8a/` and/or `armeabi-v7a/`).
3. Let Android Studio download/choose the NDK & CMake if prompted.
4. Build & run on a physical device (preferred). Grant camera permission.
5. Use the "Toggle" button to switch between Raw and Edge view.

### Web
1. `cd web`
2. `npm install`
3. `npm run start` (or `npx serve` for static)
4. Open `http://localhost:3000` and load the saved processed frame from `/docs/screenshots/processed_sample.png`.

---

## Files to inspect
- `app/src/main/java/com/princesingh/flam/MainActivity.kt` — UI, toggle, FPS overlay
- `app/src/main/java/com/princesingh/flam/Camera2Controller.kt` — Camera2 capture
- `app/src/main/java/com/princesingh/flam/GLRenderer.kt` — OpenGL ES 2.0 renderer
- `jni/native_processor.cpp` — NV21 -> OpenCV -> RGBA processing
- `web/` — TypeScript viewer

---

## Notes and Known limitations
- This is a minimal research demo to demonstrate integration; performance may vary across devices. For production use, consider zero-copy texture sharing (AImageReader / AHardwareBuffer), using EGLImage / Android GPU buffers, or using RenderScript/Vulkan.
- OpenCV native conversion is simplified but correct for most YUV->Mat flows; test on target device and inspect `rowStride/pixelStride` if needed.

---

## Suggested repo commit history
- `init: add README and repo skeleton`
- `feat(app): add Camera2 capture and texture preview`
- `feat(native): add OpenCV native processing (Canny -> RGBA output)`
- `feat(gl): add GLSurfaceView renderer and texture update`
- `feat(web): add TypeScript viewer and sample processed image`
- `docs: add screenshots and demo instructions`

### Save / Demo artifacts
- I included a small helper `SaveHelper.kt` that saves the processed RGBA frame to PNG at:
  `app/src/main/java/com/princesingh/flam/SaveHelper.kt`
- Saved sample images and demo GIF are in `/docs/screenshots/`:
  - `android_preview.png`
  - `android_processed.png`
  - `demo.gif`

### Build notes & how I tested
- See `BUILD_NOTES.md` for exact NDK and OpenCV guidance.
- See `how_i_tested.md` for detailed steps I used to capture screenshots and demo GIF.


---

## Contact
Prince Singh — prince.singh.de@gmail.com
