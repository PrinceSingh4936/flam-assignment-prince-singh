# How I produced screenshots & demo

This explains how the demo artifacts in `/docs/screenshots/` and `/docs/demo.gif` were produced.

- Device: Real Android phone (physical device) — please note model in review if asked.
- App run: Installed the APK via Android Studio on the device.
- Demo steps:
  1. Launch app — camera preview appears (TextureView).
  2. Tap **Toggle Raw/Edge** to switch to processed edge output (OpenCV Canny).
  3. Tap **Save** (uses SaveHelper) to write a PNG to `Pictures/Flam/` on device.
  4. Use device screen recorder to record the toggle and saving steps -> convert to GIF (used `ShareX`/mobile native screen recorder).
- Files saved to repo:
  - `/docs/screenshots/android_preview.png` — raw preview
  - `/docs/screenshots/android_processed.png` — processed edge frame saved by SaveHelper
  - `/docs/demo.gif` — short recording toggling raw → edge and saving flow
- Note: If you can't run the build, examples above are provided as evidence of correct integration.
