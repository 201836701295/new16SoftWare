//
// File: mfft.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "mfft.h"
#include "aweight.h"
#include "cweight.h"
#include "dspmath_data.h"
#include "dspmath_initialize.h"
#include "fft.h"
#include "mconv.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "zweight.h"
#include <string.h>

// Function Definitions

//
// Arguments    : const coder::array<float, 2U> *X
//                int N
//                coder::array<creal32_T, 2U> *F
// Return Type  : void
//
void mfft(const coder::array<float, 2U> &X, int N, coder::array<creal32_T, 2U>
&F) {
    if (!isInitialized_dspmath) {
        dspmath_initialize();
    }

    fft(X, N, F);
}

//
// File trailer for mfft.cpp
//
// [EOF]
//
