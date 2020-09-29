//
// File: mslm.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 29-Sep-2020 18:02:36
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
    int k;
    int idx;
    int firstBlockLength;
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
    coder::array<float, 2U> b_z1;
    float c_x;
    float bsum;
    if (!isInitialized_dspmath) {
        dspmath_initialize();
    }

    b_fft(x, X);
    nx = X.size(1);
    b_X.set_size(1, X.size(1));
    for (k = 0; k < nx; k++) {
        b_X[k] = rt_hypotf_snf(X[k].re, X[k].im);
    }

    idx = b_X.size(1);
    for (firstBlockLength = 0; firstBlockLength < idx; firstBlockLength++) {
        if (b_X[firstBlockLength] == 0.0F) {
            b_X[firstBlockLength] = 1.0E-17F;
        }
    }

    if (b_X.size(1) - 1 < 0) {
        temp.set_size(1, 0);
    } else {
        temp.set_size(1, (static_cast<int>(static_cast<double>(b_X.size(1)) - 1.0) +
                          1));
        idx = static_cast<int>(static_cast<double>(b_X.size(1)) - 1.0);
        for (firstBlockLength = 0; firstBlockLength <= idx; firstBlockLength++) {
            temp[firstBlockLength] = firstBlockLength;
        }
    }

    firstBlockLength = temp.size(0) * temp.size(1);
    temp.set_size(1, temp.size(1));
    d = 44100.0 / static_cast<double>(b_X.size(1));
    idx = firstBlockLength - 1;
    for (firstBlockLength = 0; firstBlockLength <= idx; firstBlockLength++) {
        temp[firstBlockLength] = d * temp[firstBlockLength];
    }

    b_x.set_size(1, temp.size(1));
    idx = temp.size(0) * temp.size(1);
    for (firstBlockLength = 0; firstBlockLength < idx; firstBlockLength++) {
        b_x[firstBlockLength] = (temp[firstBlockLength] < 22050.0);
    }

    nx = b_x.size(1);
    idx = 0;
    ii.set_size(1, b_x.size(1));
    firstBlockLength = 0;
    exitg1 = false;
    while ((!exitg1) && (firstBlockLength <= nx - 1)) {
        if (b_x[firstBlockLength]) {
            idx++;
            ii[idx - 1] = firstBlockLength + 1;
            if (idx >= nx) {
                exitg1 = true;
            } else {
                firstBlockLength++;
            }
        } else {
            firstBlockLength++;
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
    for (firstBlockLength = 0; firstBlockLength < idx; firstBlockLength++) {
        ind[firstBlockLength] = ii[firstBlockLength];
    }

    b_temp.set_size(1, ind.size(1));
    idx = ind.size(0) * ind.size(1);
    for (firstBlockLength = 0; firstBlockLength < idx; firstBlockLength++) {
        b_temp[firstBlockLength] = temp[static_cast<int>(ind[firstBlockLength]) - 1];
    }

    temp.set_size(1, b_temp.size(1));
    idx = b_temp.size(0) * b_temp.size(1);
    for (firstBlockLength = 0; firstBlockLength < idx; firstBlockLength++) {
        temp[firstBlockLength] = b_temp[firstBlockLength];
    }

    c_X.set_size(1, ind.size(1));
    idx = ind.size(0) * ind.size(1);
    for (firstBlockLength = 0; firstBlockLength < idx; firstBlockLength++) {
        c_X[firstBlockLength] = b_X[static_cast<int>(ind[firstBlockLength]) - 1];
    }

    b_X.set_size(1, c_X.size(1));
    idx = c_X.size(0) * c_X.size(1);
    for (firstBlockLength = 0; firstBlockLength < idx; firstBlockLength++) {
        b_X[firstBlockLength] = c_X[firstBlockLength];
    }

    idx = temp.size(1);
    for (firstBlockLength = 0; firstBlockLength < idx; firstBlockLength++) {
        if (temp[firstBlockLength] == 0.0) {
            temp[firstBlockLength] = 1.0E-17;
        }
    }

    // filterA
    ind.set_size(1, temp.size(1));
    idx = temp.size(0) * temp.size(1);
    for (firstBlockLength = 0; firstBlockLength < idx; firstBlockLength++) {
        ind[firstBlockLength] = temp[firstBlockLength];
    }

    temp.set_size(1, temp.size(1));
    nx = temp.size(1);
    for (k = 0; k < nx; k++) {
        d = ind[k];
        temp[k] = d * d;
    }

    z1.set_size(1, temp.size(1));
    nx = temp.size(1);
    for (k = 0; k < nx; k++) {
        z1[k] = rt_powd_snf(temp[k], 4.0);
    }

    ind.set_size(1, temp.size(1));
    idx = temp.size(0) * temp.size(1);
    for (firstBlockLength = 0; firstBlockLength < idx; firstBlockLength++) {
        ind[firstBlockLength] = temp[firstBlockLength] + 424.31867740600904;
    }

    b_temp.set_size(1, ind.size(1));
    nx = ind.size(1);
    for (k = 0; k < nx; k++) {
        d = ind[k];
        b_temp[k] = d * d;
    }

    ind.set_size(1, temp.size(1));
    idx = temp.size(0) * temp.size(1);
    for (firstBlockLength = 0; firstBlockLength < idx; firstBlockLength++) {
        ind[firstBlockLength] = temp[firstBlockLength] + 1.4869892824308902E+8;
    }

    y.set_size(1, ind.size(1));
    nx = ind.size(1);
    for (k = 0; k < nx; k++) {
        d = ind[k];
        y[k] = d * d;
    }

    b_X.set_size(1, z1.size(1));
    idx = z1.size(0) * z1.size(1) - 1;
    for (firstBlockLength = 0; firstBlockLength <= idx; firstBlockLength++) {
        d = temp[firstBlockLength];
        b_X[firstBlockLength] = static_cast<float>(3.5041384E+16 *
                                                   z1[firstBlockLength] /
                                                   (b_temp[firstBlockLength] *
                                                    (d + 11589.0930520225) *
                                                    (d + 544440.67046057282) *
                                                    y[firstBlockLength])) * b_X[firstBlockLength];
    }

    b_z1.set_size(1, b_X.size(1));
    nx = b_X.size(1);
    for (k = 0; k < nx; k++) {
        c_x = b_X[k];
        b_z1[k] = c_x * c_x;
    }

    if (b_z1.size(1) == 0) {
        c_x = 0.0F;
    } else {
        int nblocks;
        int lastBlockLength;
        if (b_z1.size(1) <= 1024) {
            firstBlockLength = b_z1.size(1);
            lastBlockLength = 0;
            nblocks = 1;
        } else {
            firstBlockLength = 1024;
            nblocks = b_z1.size(1) / 1024;
            lastBlockLength = b_z1.size(1) - (nblocks << 10);
            if (lastBlockLength > 0) {
                nblocks++;
            } else {
                lastBlockLength = 1024;
            }
        }

        c_x = b_z1[0];
        for (k = 2; k <= firstBlockLength; k++) {
            c_x += b_z1[k - 1];
        }

        for (firstBlockLength = 2; firstBlockLength <= nblocks; firstBlockLength++) {
            idx = (firstBlockLength - 1) << 10;
            bsum = b_z1[idx];
            if (firstBlockLength == nblocks) {
                nx = lastBlockLength;
            } else {
                nx = 1024;
            }

            for (k = 2; k <= nx; k++) {
                bsum += b_z1[(idx + k) - 1];
            }

            c_x += bsum;
        }
    }

    return 10.0F * std::log10(c_x / static_cast<float>(b_X.size(1)) / static_cast<
            float>(2.2675736961451248E-5 * static_cast<double>(x.size(1))));
}

//
// File trailer for mslm.cpp
//
// [EOF]
//
