//
// File: dspmath_rtwutil.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "dspmath_rtwutil.h"
#include "aweight.h"
#include "cweight.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "zweight.h"
#include <cmath>
#include <math.h>
#include <string.h>

// Function Definitions

//
// Arguments    : double u0
//                double u1
// Return Type  : double
//
double rt_hypotd_snf(double u0, double u1) {
    double y;
    double a;
    a = std::abs(u0);
    y = std::abs(u1);
    if (a < y) {
        a /= y;
        y *= std::sqrt(a * a + 1.0);
    } else if (a > y) {
        y /= a;
        y = a * std::sqrt(y * y + 1.0);
    } else {
        if (!rtIsNaN(y)) {
            y = a * 1.4142135623730951;
        }
    }

    return y;
}

//
// Arguments    : double u0
//                double u1
// Return Type  : double
//
double rt_powd_snf(double u0, double u1) {
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
// File trailer for dspmath_rtwutil.cpp
//
// [EOF]
//
