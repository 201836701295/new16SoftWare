//
// File: mifft.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 28-Sep-2020 16:47:53
//

// Include Files
#include "mifft.h"
#include "dspmath_data.h"
#include "dspmath_initialize.h"
#include "ifft.h"
#include "mconv.h"
#include "mfft.h"
#include "mslm.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include <string.h>

// Function Definitions

//
// Arguments    : const coder::array<creal32_T, 2U> *F
//                int N
//                coder::array<creal32_T, 2U> *X
// Return Type  : void
//
void mifft(const coder::array<creal32_T, 2U> &F, int N, coder::array<creal32_T,
        2U> &X) {
    if (!isInitialized_dspmath) {
        dspmath_initialize();
    }

    ifft(F, N, X);
}

//
// File trailer for mifft.cpp
//
// [EOF]
//
