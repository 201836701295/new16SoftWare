//
// File: dspmath_terminate.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "dspmath_terminate.h"
#include "aweight.h"
#include "cweight.h"
#include "dspmath_data.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "zweight.h"
#include <string.h>

// Function Definitions

//
// Arguments    : void
// Return Type  : void
//
void dspmath_terminate() {
    // (no terminate code required)
    isInitialized_dspmath = false;
}

//
// File trailer for dspmath_terminate.cpp
//
// [EOF]
//
