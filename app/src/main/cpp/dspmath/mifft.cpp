//
// File: mifft.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "mifft.h"
#include "aweight.h"
#include "cweight.h"
#include "dspmath_data.h"
#include "dspmath_initialize.h"
#include "ifft.h"
#include "mconv.h"
#include "mfft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "zweight.h"
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
