//
// File: psdfreqvec.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 29-Sep-2020 18:02:36
//

// Include Files
#include "psdfreqvec.h"
#include "computepsd.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mslm.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include <cfloat>
#include <cmath>
#include <string.h>

// Function Declarations
static double rt_remd_snf(double u0, double u1);

// Function Definitions

//
// Arguments    : double u0
//                double u1
// Return Type  : double
//
static double rt_remd_snf(double u0, double u1) {
    double y;
    if (rtIsNaN(u0) || rtIsNaN(u1) || rtIsInf(u0)) {
        y = rtNaN;
    } else if (rtIsInf(u1)) {
        y = u0;
    } else {
        double b_u1;
        if (u1 < 0.0) {
            b_u1 = std::ceil(u1);
        } else {
            b_u1 = std::floor(u1);
        }

        if ((u1 != 0.0) && (u1 != b_u1)) {
            b_u1 = std::abs(u0 / u1);
            if (!(std::abs(b_u1 - std::floor(b_u1 + 0.5)) > DBL_EPSILON * b_u1)) {
                y = 0.0 * u0;
            } else {
                y = std::fmod(u0, u1);
            }
        } else {
            y = std::fmod(u0, u1);
        }
    }

    return y;
}

//
// Arguments    : double varargin_2
//                double varargin_4
//                coder::array<double, 1U> *w
// Return Type  : void
//
void psdfreqvec(double varargin_2, double varargin_4, coder::array<double, 1U>
&w) {
    double Fs1;
    double freq_res;
    coder::array<double, 2U> w1;
    int loop_ub;
    int i;
    double Nyq;
    double half_res;
    boolean_T isNPTSodd;
    if (rtIsNaN(varargin_4)) {
        Fs1 = 6.2831853071795862;
    } else {
        Fs1 = varargin_4;
    }

    freq_res = Fs1 / varargin_2;
    if (rtIsNaN(varargin_2 - 1.0)) {
        w1.set_size(1, 1);
        w1[0] = rtNaN;
    } else if (varargin_2 - 1.0 < 0.0) {
        w1.set_size(1, 0);
    } else if (rtIsInf(varargin_2 - 1.0) && (0.0 == varargin_2 - 1.0)) {
        w1.set_size(1, 1);
        w1[0] = rtNaN;
    } else {
        loop_ub = static_cast<int>(std::floor(varargin_2 - 1.0));
        w1.set_size(1, (loop_ub + 1));
        for (i = 0; i <= loop_ub; i++) {
            w1[i] = i;
        }
    }

    i = w1.size(0) * w1.size(1);
    w1.set_size(1, w1.size(1));
    loop_ub = i - 1;
    for (i = 0; i <= loop_ub; i++) {
        w1[i] = freq_res * w1[i];
    }

    Nyq = Fs1 / 2.0;
    half_res = freq_res / 2.0;
    isNPTSodd = (rt_remd_snf(varargin_2, 2.0) != 0.0);
    if (isNPTSodd) {
        double halfNPTS;
        halfNPTS = (varargin_2 + 1.0) / 2.0;
        w1[static_cast<int>(halfNPTS) - 1] = Nyq - half_res;
        w1[static_cast<int>(static_cast<unsigned int>(halfNPTS))] = Nyq + half_res;
    } else {
        double halfNPTS;
        halfNPTS = varargin_2 / 2.0 + 1.0;
        w1[static_cast<int>(halfNPTS) - 1] = Nyq;
    }

    w1[static_cast<int>(varargin_2) - 1] = Fs1 - freq_res;
    w.set_size(w1.size(1));
    loop_ub = w1.size(1);
    for (i = 0; i < loop_ub; i++) {
        w[i] = w1[i];
    }
}

//
// File trailer for psdfreqvec.cpp
//
// [EOF]
//
