//
// File: welchparse.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "welchparse.h"
#include "FFTImplementationCallback.h"
#include "aweight.h"
#include "cweight.h"
#include "gencoswin.h"
#include "ifft.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "octbank.h"
#include "rt_nonfinite.h"
#include "welch.h"
#include "zweight.h"
#include <cmath>
#include <math.h>
#include <string.h>

// Function Declarations
static int div_s32_floor(int numerator, int denominator);

// Function Definitions

//
// Arguments    : int numerator
//                int denominator
// Return Type  : int
//
static int div_s32_floor(int numerator, int denominator) {
    int quotient;
    unsigned int absNumerator;
    if (denominator == 0) {
        if (numerator >= 0) {
            quotient = MAX_int32_T;
        } else {
            quotient = MIN_int32_T;
        }
    } else {
        unsigned int absDenominator;
        boolean_T quotientNeedsNegation;
        unsigned int tempAbsQuotient;
        if (numerator < 0) {
            absNumerator = ~static_cast<unsigned int>(numerator) + 1U;
        } else {
            absNumerator = static_cast<unsigned int>(numerator);
        }

        if (denominator < 0) {
            absDenominator = ~static_cast<unsigned int>(denominator) + 1U;
        } else {
            absDenominator = static_cast<unsigned int>(denominator);
        }

        quotientNeedsNegation = ((numerator < 0) != (denominator < 0));
        tempAbsQuotient = absNumerator / absDenominator;
        if (quotientNeedsNegation) {
            absNumerator %= absDenominator;
            if (absNumerator > 0U) {
                tempAbsQuotient++;
            }

            quotient = -static_cast<int>(tempAbsQuotient);
        } else {
            quotient = static_cast<int>(tempAbsQuotient);
        }
    }

    return quotient;
}

//
// Arguments    : const coder::array<float, 2U> *x1
//                int varargin_3
//                int varargin_4
//                coder::array<float, 1U> *x
//                double *M
//                coder::array<double, 1U> *win
//                double *noverlap
//                double *k
//                double *L
//                struct_T *options
// Return Type  : void
//
void welchparse(const coder::array<float, 2U> &x1, int varargin_3, int
varargin_4, coder::array<float, 1U> &x, double *M, coder::array<
        double, 1U> &win, double *noverlap, double *k, double *L,
                struct_T *options) {
    int loop_ub;
    int i;
    int b_M;
    int b_L;
    int b_noverlap;
    coder::array<double, 1U> b_win;
    int eint;
    static const char cv[8] = {'o', 'n', 'e', 's', 'i', 'd', 'e', 'd'};

    x.set_size(x1.size(1));
    loop_ub = x1.size(1);
    for (i = 0; i < loop_ub; i++) {
        x[i] = x1[i];
    }

    b_M = x1.size(1);
    b_L = static_cast<int>(std::floor(static_cast<double>(x1.size(1)) / 4.5));
    b_noverlap = static_cast<int>(std::floor(0.5 * static_cast<double>(b_L)));
    if (b_L == 0) {
        *L = 0.0;
    } else {
        *L = std::fmod(static_cast<double>(b_L), 2.0);
    }

    if (*L == 0.0) {
        calc_window(static_cast<double>(b_L) / 2.0, static_cast<double>(b_L), win);
        b_win.set_size(((win.size(0) + div_s32_floor(1 - win.size(0), -1)) + 1));
        loop_ub = win.size(0);
        for (i = 0; i < loop_ub; i++) {
            b_win[i] = win[i];
        }

        loop_ub = div_s32_floor(1 - win.size(0), -1);
        for (i = 0; i <= loop_ub; i++) {
            b_win[i + win.size(0)] = win[(win.size(0) - i) - 1];
        }

        win.set_size(b_win.size(0));
        loop_ub = b_win.size(0);
        for (i = 0; i < loop_ub; i++) {
            win[i] = b_win[i];
        }
    } else {
        int i1;
        int i2;
        int b_loop_ub;
        calc_window((static_cast<double>(b_L) + 1.0) / 2.0, static_cast<double>(b_L),
                    win);
        if (1 > win.size(0) - 1) {
            i = 0;
            i1 = 1;
            i2 = -1;
        } else {
            i = win.size(0) - 2;
            i1 = -1;
            i2 = 0;
        }

        loop_ub = div_s32_floor(i2 - i, i1);
        b_win.set_size(((win.size(0) + loop_ub) + 1));
        b_loop_ub = win.size(0);
        for (i2 = 0; i2 < b_loop_ub; i2++) {
            b_win[i2] = win[i2];
        }

        for (i2 = 0; i2 <= loop_ub; i2++) {
            b_win[i2 + win.size(0)] = win[i + i1 * i2];
        }

        win.set_size(b_win.size(0));
        loop_ub = b_win.size(0);
        for (i = 0; i < loop_ub; i++) {
            win[i] = b_win[i];
        }
    }

    frexp(static_cast<double>(b_L), &eint);
    options->average = true;
    options->maxhold = false;
    options->minhold = false;
    options->MIMO = false;
    options->conflevel = rtNaN;
    for (i = 0; i < 8; i++) {
        options->range[i] = cv[i];
    }

    options->centerdc = false;
    options->nfft = varargin_3;
    options->isNFFTSingle = false;
    options->Fs = varargin_4;
    *k = (static_cast<double>(x1.size(1)) - static_cast<double>(b_noverlap)) /
         static_cast<double>(b_L - b_noverlap);
    if (*k < 0.0) {
        *k = std::ceil(*k);
    } else {
        *k = std::floor(*k);
    }

    *M = b_M;
    *noverlap = b_noverlap;
    *L = b_L;
}

//
// File trailer for welchparse.cpp
//
// [EOF]
//
