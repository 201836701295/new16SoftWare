//
// File: aweight.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "aweight.h"
#include "cweight.h"
#include "dspmath_data.h"
#include "dspmath_initialize.h"
#include "dspmath_rtwutil.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "octbank.h"
#include "rt_nonfinite.h"
#include "zweight.h"
#include <cmath>
#include <string.h>

// Function Definitions

//
// Arguments    : const coder::array<short, 2U> *x
//                float p[8]
//                float ff[8]
//                float *Lp
// Return Type  : void
//
void aweight(const coder::array<short, 2U> &x, float p[8], float ff[8], float
*Lp) {
    double b_p[8];
    double b_ff[8];
    int k;
    double y;
    double LA[8];
    static const double weight[8] = {-26.2, -16.1, -8.6, -3.2, 0.0, 1.2, 1.0,
                                     -1.1};

    if (!isInitialized_dspmath) {
        dspmath_initialize();
    }

    octbank(x, b_p, b_ff);
    for (k = 0; k < 8; k++) {
        LA[k] = rt_powd_snf(10.0, 0.1 * (b_p[k] + weight[k]));
    }

    y = LA[0];
    for (k = 0; k < 7; k++) {
        y += LA[k + 1];
    }

    for (k = 0; k < 8; k++) {
        p[k] = static_cast<float>(b_p[k]);
        ff[k] = static_cast<float>(b_ff[k]);
    }

    *Lp = static_cast<float>(10.0 * std::log10(y));
}

//
// File trailer for aweight.cpp
//
// [EOF]
//
