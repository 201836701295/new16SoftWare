#include <jni.h>
#include <cmath>
#include <string>
#include <sstream>
#include "dspmath/mconv.h"
#include "dspmath/mfft.h"
#include "dspmath/mifft.h"
#include "dspmath/mywelch.h"
#include "dspmath/aweight.h"
#include "dspmath/zweight.h"
#include "dspmath/cweight.h"
#include <android/log.h>

#define LOG_TAG  "C_TAG"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Java_edu_scut_acoustics_utils_DSPMath_conv(JNIEnv *env, jobject, jfloatArray a,
                                           jfloatArray b, jfloatArray c) {
    const int n = env->GetArrayLength(c);
    const int la = env->GetArrayLength(a), lb = env->GetArrayLength(b);

    coder::array<float, 2U> xa, xb;
    coder::array<float, 2U> xc;

    jfloat *arra = env->GetFloatArrayElements(a, nullptr);
    jfloat *arrb = env->GetFloatArrayElements(b, nullptr);
    jfloat *arrc = env->GetFloatArrayElements(c, nullptr);

    xa.set(arra, 1, la);
    xb.set(arrb, 1, lb);
    xc.set(arrc, 1, n);

    mconv(xa, xb, n, xc);

    env->ReleaseFloatArrayElements(a, arra, JNI_ABORT);
    env->ReleaseFloatArrayElements(b, arrb, JNI_ABORT);
    env->ReleaseFloatArrayElements(c, arrc, JNI_OK);
}

extern "C"
JNIEXPORT void JNICALL
Java_edu_scut_acoustics_utils_DSPMath_fft(JNIEnv *env, jobject /* this */, jfloatArray x, jint n,
                                          jfloatArray re, jfloatArray im) {
    //获得数组长度
    const int xlength = env->GetArrayLength(x);
    const int flength = env->GetArrayLength(re);

    coder::array<float, 2U> X;
    coder::array<creal32_T, 2U> F;

    jfloat *arr = env->GetFloatArrayElements(x, nullptr);
    X.set(arr, 1, xlength);

    //进行fft
    mfft(X, n, F);

    //释放内存
    env->ReleaseFloatArrayElements(x, arr, JNI_ABORT);

    jfloat *rearr = env->GetFloatArrayElements(re, nullptr);
    jfloat *imarr = env->GetFloatArrayElements(im, nullptr);

    for (int i = 0; i < F.size(1) && i < flength; ++i) {
        rearr[i] = F[i].re;
        imarr[i] = F[i].im;
    }

    //释放内存
    env->ReleaseFloatArrayElements(re, rearr, JNI_OK);
    env->ReleaseFloatArrayElements(im, imarr, JNI_OK);
}

extern "C"
JNIEXPORT void JNICALL
Java_edu_scut_acoustics_utils_DSPMath_ifft(JNIEnv *env, jobject /* this */, jfloatArray re,
                                           jfloatArray im, jint n, jfloatArray x) {
    const int xlength = env->GetArrayLength(x);
    const int flength = env->GetArrayLength(re);

    coder::array<creal32_T, 2U> F, X;
    F.set_size(1, flength);
    jfloat *arrR = env->GetFloatArrayElements(re, nullptr);
    jfloat *arrI = env->GetFloatArrayElements(im, nullptr);

    for (int i = 0; i < flength; ++i) {
        F[i].re = arrR[i];
        F[i].im = arrI[i];
    }

    env->ReleaseFloatArrayElements(re, arrR, JNI_ABORT);
    env->ReleaseFloatArrayElements(im, arrI, JNI_ABORT);

    mifft(F, n, X);

    jfloat *arrX = env->GetFloatArrayElements(im, nullptr);
    for (int i = 0; i < xlength && i < n; ++i) {
        arrX[i] = X[i].re;
    }
    env->ReleaseFloatArrayElements(x, arrX, JNI_OK);
}

using std::string;
using std::stringstream;
extern "C"
JNIEXPORT void JNICALL
Java_edu_scut_acoustics_utils_DSPMath_welch(JNIEnv *env, jobject /* this */, jfloatArray x, jint n,
                                            jint fs, jfloatArray pxx, jfloatArray f) {
    int xl = env->GetArrayLength(x);
    int pl = env->GetArrayLength(pxx);
    int fl = env->GetArrayLength(f);

    coder::array<float, 2U> X, PXX;
    coder::array<float, 1U> F;

    jfloat *xarr = env->GetFloatArrayElements(x, nullptr);
    X.set(xarr, 1, xl);

    mywelch(X, n, fs, PXX, F);

    string text1 = "PXX length";
    string text2 = "F length";
    LOGD("PXX length=%d", PXX.size(1) * PXX.size(0));
    LOGD("F length=%d", F.size(0));

    env->ReleaseFloatArrayElements(x, xarr, JNI_ABORT);

    jfloat *parr = env->GetFloatArrayElements(pxx, nullptr);
    jfloat *farr = env->GetFloatArrayElements(f, nullptr);

    for (int i = 0; i < pl && i < n && i < PXX.size(1) * PXX.size(0); ++i) {
        parr[i] = PXX[i];
    }
    for (int i = 0; i < fl && i < n && i < F.size(0); ++i) {
        farr[i] = F[i];
    }

    env->ReleaseFloatArrayElements(pxx, parr, JNI_OK);
    env->ReleaseFloatArrayElements(f, farr, JNI_OK);
}

using std::min;
using std::atan;
extern "C"
JNIEXPORT void JNICALL
Java_edu_scut_acoustics_utils_DSPMath_phase(JNIEnv *env, jobject thiz, jfloatArray re,
                                            jfloatArray im, jfloatArray rad) {
    int rel = env->GetArrayLength(re);
    int iml = env->GetArrayLength(im);
    int ral = env->GetArrayLength(rad);
    int temp = (min(rel, iml) + 1) / 2;
    int arrLength = min(temp, ral);

    jfloat *reArr = env->GetFloatArrayElements(re, nullptr);
    jfloat *imArr = env->GetFloatArrayElements(im, nullptr);
    jfloat *radArr = env->GetFloatArrayElements(rad, nullptr);

    for (int i = 0; i < arrLength; ++i) {
        radArr[i] = atan(imArr[i] / reArr[i]);
    }

    env->ReleaseFloatArrayElements(rad, radArr, JNI_OK);
    env->ReleaseFloatArrayElements(re, reArr, JNI_ABORT);
    env->ReleaseFloatArrayElements(im, imArr, JNI_ABORT);
}

extern "C"
JNIEXPORT jfloat JNICALL
Java_edu_scut_acoustics_utils_DSPMath_aweight(JNIEnv *env, jobject thiz, jshortArray x,
                                              jfloatArray l8, jfloatArray ff) {
    const int n = env->GetArrayLength(x);
    jshort *xArr = env->GetShortArrayElements(x, nullptr);
    jfloat *l8Arr = env->GetFloatArrayElements(l8, nullptr);
    jfloat *ffArr = env->GetFloatArrayElements(ff, nullptr);

    coder::array<short, 2U> xx;
    xx.set(xArr, 1, n);

    jfloat temp;

    aweight(xx, l8Arr, ffArr, &temp);

    env->ReleaseShortArrayElements(x, xArr, JNI_ABORT);
    env->ReleaseFloatArrayElements(l8, l8Arr, JNI_OK);
    env->ReleaseFloatArrayElements(ff, ffArr, JNI_OK);

    return temp;
}

extern "C"
JNIEXPORT jfloat JNICALL
Java_edu_scut_acoustics_utils_DSPMath_zweight(JNIEnv *env, jobject thiz, jshortArray x,
                                              jfloatArray l8, jfloatArray ff) {
    const int n = env->GetArrayLength(x);
    jshort *xArr = env->GetShortArrayElements(x, nullptr);
    jfloat *l8Arr = env->GetFloatArrayElements(l8, nullptr);
    jfloat *ffArr = env->GetFloatArrayElements(ff, nullptr);

    coder::array<short, 2U> xx;
    xx.set(xArr, 1, n);

    jfloat temp;

    zweight(xx, l8Arr, ffArr, &temp);

    env->ReleaseShortArrayElements(x, xArr, JNI_ABORT);
    env->ReleaseFloatArrayElements(l8, l8Arr, JNI_OK);
    env->ReleaseFloatArrayElements(ff, ffArr, JNI_OK);

    return temp;
}

extern "C"
JNIEXPORT jfloat JNICALL
Java_edu_scut_acoustics_utils_DSPMath_cweight(JNIEnv *env, jobject thiz, jshortArray x,
                                              jfloatArray l8, jfloatArray ff) {
    const int n = env->GetArrayLength(x);
    jshort *xArr = env->GetShortArrayElements(x, nullptr);
    jfloat *l8Arr = env->GetFloatArrayElements(l8, nullptr);
    jfloat *ffArr = env->GetFloatArrayElements(ff, nullptr);

    coder::array<short, 2U> xx;
    xx.set(xArr, 1, n);

    jfloat temp;

    cweight(xx, l8Arr, ffArr, &temp);

    env->ReleaseShortArrayElements(x, xArr, JNI_ABORT);
    env->ReleaseFloatArrayElements(l8, l8Arr, JNI_OK);
    env->ReleaseFloatArrayElements(ff, ffArr, JNI_OK);

    return temp;
}