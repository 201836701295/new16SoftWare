//
// File: mfft.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 16-Sep-2020 10:10:07
//

// Include Files
#include "mfft.h"
#include "dspmath_data.h"
#include "dspmath_initialize.h"
#include "fft.h"
#include "mconv.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
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
