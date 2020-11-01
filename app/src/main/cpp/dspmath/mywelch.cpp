//
// File: mywelch.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "mywelch.h"
#include "FFTImplementationCallback.h"
#include "aweight.h"
#include "cweight.h"
#include "dspmath_data.h"
#include "dspmath_initialize.h"
#include "ifft.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "rt_nonfinite.h"
#include "welch.h"
#include "zweight.h"
#include <cmath>
#include <string.h>

// Function Definitions

//
// Arguments    : const coder::array<float, 2U> *x
//                int N
//                int fs
//                coder::array<float, 2U> *pxx
//                coder::array<float, 1U> *f
// Return Type  : void
//
void mywelch(const coder::array<float, 2U> &x, int N, int fs, coder::array<float,
        2U> &pxx, coder::array<float, 1U> &f) {
    int nx;
    int k;
    if (!isInitialized_dspmath) {
        dspmath_initialize();
    }

    welch(x, N, fs, pxx, f);
    nx = pxx.size(0) * pxx.size(1);
    for (k = 0; k < nx; k++) {
        pxx[k] = std::log10(pxx[k]);
    }

    nx = pxx.size(0) * pxx.size(1);
    for (k = 0; k < nx; k++) {
        pxx[k] = 10.0F * pxx[k];
    }
}

//
// File trailer for mywelch.cpp
//
// [EOF]
//
