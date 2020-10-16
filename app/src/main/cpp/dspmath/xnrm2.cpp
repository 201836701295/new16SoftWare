//
// File: xnrm2.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 15-Oct-2020 21:35:42
//

// Include Files
#include "xnrm2.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "slmfunc.h"
#include <cmath>
#include <string.h>

// Function Definitions

//
// Arguments    : int n
//                const double x[3]
// Return Type  : double
//
double b_xnrm2(int n, const double x[3]) {
    double y;
    y = 0.0;
    if (n >= 1) {
        if (n == 1) {
            y = std::abs(x[1]);
        } else {
            double scale;
            double absxk;
            double t;
            scale = 3.3121686421112381E-170;
            absxk = std::abs(x[1]);
            if (absxk > 3.3121686421112381E-170) {
                y = 1.0;
                scale = absxk;
            } else {
                t = absxk / 3.3121686421112381E-170;
                y = t * t;
            }

            absxk = std::abs(x[2]);
            if (absxk > scale) {
                t = scale / absxk;
                y = y * t * t + 1.0;
                scale = absxk;
            } else {
                t = absxk / scale;
                y += t * t;
            }

            y = scale * std::sqrt(y);
        }
    }

    return y;
}

//
// Arguments    : int n
//                const double x[36]
//                int ix0
// Return Type  : double
//
double xnrm2(int n, const double x[36], int ix0) {
    double y;
    y = 0.0;
    if (n >= 1) {
        if (n == 1) {
            y = std::abs(x[ix0 - 1]);
        } else {
            double scale;
            int kend;
            scale = 3.3121686421112381E-170;
            kend = (ix0 + n) - 1;
            for (int k = ix0; k <= kend; k++) {
                double absxk;
                absxk = std::abs(x[k - 1]);
                if (absxk > scale) {
                    double t;
                    t = scale / absxk;
                    y = y * t * t + 1.0;
                    scale = absxk;
                } else {
                    double t;
                    t = absxk / scale;
                    y += t * t;
                }
            }

            y = scale * std::sqrt(y);
        }
    }

    return y;
}

//
// File trailer for xnrm2.cpp
//
// [EOF]
//
