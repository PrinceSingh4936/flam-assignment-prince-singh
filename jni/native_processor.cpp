#include <jni.h>
#include <vector>
#include <android/log.h>
#include <opencv2/opencv.hpp>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "native-lib", __VA_ARGS__)

using namespace cv;
using namespace std;

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_princesingh_flam_MainActivity_processFrameNV21(JNIEnv *env, jobject /* this */,
                                                       jbyteArray nv21_, jint width, jint height) {
    jbyte *nv21 = env->GetByteArrayElements(nv21_, NULL);
    jsize len = env->GetArrayLength(nv21_);

    // Create Mat from NV21 data (YUV420sp)
    Mat yuv(height + height / 2, width, CV_8UC1, (unsigned char *) nv21);
    Mat bgr;
    cvtColor(yuv, bgr, COLOR_YUV2BGR_NV21);

    Mat gray, blurMat, edges;
    cvtColor(bgr, gray, COLOR_BGR2GRAY);
    GaussianBlur(gray, blurMat, Size(5, 5), 0);
    Canny(blurMat, edges, 50, 150);

    // Convert edges (single channel) to RGBA for rendering
    Mat rgba;
    cvtColor(edges, rgba, COLOR_GRAY2RGBA);

    int outSize = rgba.total() * rgba.elemSize(); // width * height * 4
    jbyteArray outArray = env->NewByteArray(outSize);
    env->SetByteArrayRegion(outArray, 0, outSize, reinterpret_cast<const jbyte *>(rgba.data));

    env->ReleaseByteArrayElements(nv21_, nv21, 0);
    return outArray;
}
