//
// File: mslm.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 28-Sep-2020 16:47:53
//

// Include Files
#include "mslm.h"
#include "FFTImplementationCallback.h"
#include "dspmath_data.h"
#include "dspmath_initialize.h"
#include "fft.h"
#include "ifft.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include <cmath>
#include <math.h>
#include <string.h>

// Function Declarations
static float rt_hypotf_snf(float u0, float u1);

static double rt_powd_snf(double u0, double u1);

// Function Definitions

//
// Arguments    : float u0
//                float u1
// Return Type  : float
//
static float rt_hypotf_snf(float u0, float u1) {
    float y;
    float a;
    a = std::abs(u0);
    y = std::abs(u1);
    if (a < y) {
        a /= y;
        y *= std::sqrt(a * a + 1.0F);
    } else if (a > y) {
        y /= a;
        y = a * std::sqrt(y * y + 1.0F);
    } else {
        if (!rtIsNaNF(y)) {
            y = a * 1.41421354F;
        }
    }

    return y;
}

//
// Arguments    : double u0
//                double u1
// Return Type  : double
//
static double rt_powd_snf(double u0, double u1) {
    double y;
    if (rtIsNaN(u0) || rtIsNaN(u1)) {
        y = rtNaN;
    } else {
        double d;
        double d1;
        d = std::abs(u0);
        d1 = std::abs(u1);
        if (rtIsInf(u1)) {
            if (d == 1.0) {
                y = 1.0;
            } else if (d > 1.0) {
                if (u1 > 0.0) {
                    y = rtInf;
                } else {
                    y = 0.0;
                }
            } else if (u1 > 0.0) {
                y = 0.0;
            } else {
                y = rtInf;
            }
        } else if (d1 == 0.0) {
            y = 1.0;
        } else if (d1 == 1.0) {
            if (u1 > 0.0) {
                y = u0;
            } else {
                y = 1.0 / u0;
            }
        } else if (u1 == 2.0) {
            y = u0 * u0;
        } else if ((u1 == 0.5) && (u0 >= 0.0)) {
            y = std::sqrt(u0);
        } else if ((u0 < 0.0) && (u1 > std::floor(u1))) {
            y = rtNaN;
        } else {
            y = pow(u0, u1);
        }
    }

    return y;
}

//
// Arguments    : const coder::array<float, 2U> *x
// Return Type  : float
//
float mslm(const coder::array<float, 2U> &x) {
    coder::array<creal32_T, 2U> X;
    int nx;
    coder::array<float, 2U> b_X;
    int idx;
    int i;
    coder::array<double, 2U> temp;
    double d;
    coder::array<boolean_T, 2U> b_x;
    coder::array<int, 2U> ii;
    boolean_T exitg1;
    coder::array<double, 2U> ind;
    coder::array<double, 2U> b_temp;
    coder::array<float, 2U> c_X;
    coder::array<double, 2U> z1;
    coder::array<double, 2U> y;
    if (!isInitialized_dspmath) {
        dspmath_initialize();
    }

    b_fft(x, X);
    nx = X.size(1);
    b_X.set_size(1, X.size(1));
    for (idx = 0; idx < nx; idx++) {
        b_X[idx] = rt_hypotf_snf(X[idx].re, X[idx].im);
    }

    idx = b_X.size(1);
    for (i = 0; i < idx; i++) {
        if (b_X[i] == 0.0F) {
            b_X[i] = 1.0E-17F;
        }
    }

    if (b_X.size(1) - 1 < 0) {
        temp.set_size(1, 0);
    } else {
        temp.set_size(1, (static_cast<int>(static_cast<double>(b_X.size(1)) - 1.0) +
                          1));
        idx = static_cast<int>(static_cast<double>(b_X.size(1)) - 1.0);
        for (i = 0; i <= idx; i++) {
            temp[i] = i;
        }
    }

    i = temp.size(0) * temp.size(1);
    temp.set_size(1, temp.size(1));
    d = 44100.0 / static_cast<double>(b_X.size(1));
    idx = i - 1;
    for (i = 0; i <= idx; i++) {
        temp[i] = d * temp[i];
    }

    b_x.set_size(1, temp.size(1));
    idx = temp.size(0) * temp.size(1);
    for (i = 0; i < idx; i++) {
        b_x[i] = (temp[i] < 22050.0);
    }

    nx = b_x.size(1);
    idx = 0;
    ii.set_size(1, b_x.size(1));
    i = 0;
    exitg1 = false;
    while ((!exitg1) && (i <= nx - 1)) {
        if (b_x[i]) {
            idx++;
            ii[idx - 1] = i + 1;
            if (idx >= nx) {
                exitg1 = true;
            } else {
                i++;
            }
        } else {
            i++;
        }
    }

    if (b_x.size(1) == 1) {
        if (idx == 0) {
            ii.set_size(1, 0);
        }
    } else {
        if (1 > idx) {
            idx = 0;
        }

        ii.set_size(ii.size(0), idx);
    }

    ind.set_size(1, ii.size(1));
    idx = ii.size(0) * ii.size(1);
    for (i = 0; i < idx; i++) {
        ind[i] = ii[i];
    }

    b_temp.set_size(1, ind.size(1));
    idx = ind.size(0) * ind.size(1);
    for (i = 0; i < idx; i++) {
        b_temp[i] = temp[static_cast<int>(ind[i]) - 1];
    }

    temp.set_size(1, b_temp.size(1));
    idx = b_temp.size(0) * b_temp.size(1);
    for (i = 0; i < idx; i++) {
        temp[i] = b_temp[i];
    }

    c_X.set_size(1, ind.size(1));
    idx = ind.size(0) * ind.size(1);
    for (i = 0; i < idx; i++) {
        c_X[i] = b_X[static_cast<int>(ind[i]) - 1];
    }

    b_X.set_size(1, c_X.size(1));
    idx = c_X.size(0) * c_X.size(1);
    for (i = 0; i < idx; i++) {
        b_X[i] = c_X[i];
    }

    idx = temp.size(1);
    for (i = 0; i < idx; i++) {
        if (temp[i] == 0.0) {
            temp[i] = 1.0E-17;
        }
    }

    // filterA
    ind.set_size(1, temp.size(1));
    idx = temp.size(0) * temp.size(1);
    for (i = 0; i < idx; i++) {
        ind[i] = temp[i];
    }

    temp.set_size(1, temp.size(1));
    nx = temp.size(1);
    for (idx = 0; idx < nx; idx++) {
        d = ind[idx];
        temp[idx] = d * d;
    }

    z1.set_size(1, temp.size(1));
    nx = temp.size(1);
    for (idx = 0; idx < nx; idx++) {
        z1[idx] = rt_powd_snf(temp[idx], 4.0);
    }

    ind.set_size(1, temp.size(1));
    idx = temp.size(0) * temp.size(1);
    for (i = 0; i < idx; i++) {
        ind[i] = temp[i] + 424.31867740600904;
    }

    b_temp.set_size(1, ind.size(1));
    nx = ind.size(1);
    for (idx = 0; idx < nx; idx++) {
        d = ind[idx];
        b_temp[idx] = d * d;
    }

    ind.set_size(1, temp.size(1));
    idx = temp.size(0) * temp.size(1);
    for (i = 0; i < idx; i++) {
        ind[i] = temp[i] + 1.4869892824308902E+8;
    }

    y.set_size(1, ind.size(1));
    nx = ind.size(1);
    for (idx = 0; idx < nx; idx++) {
        d = ind[idx];
        y[idx] = d * d;
    }

    float a;
    a = static_cast<float>(3.5041384E+16 * z1[0] / (b_temp[0] * (temp[0] +
                                                                 11589.0930520225) *
                                                    (temp[0] + 544440.67046057282) * y[0])) *
        b_X[0];
    return 10.0F * std::log10(a * a / static_cast<float>(2.2675736961451248E-5 *
                                                         static_cast<double>(x.size(1))));
}

//
// File trailer for mslm.cpp
//
// [EOF]
//
