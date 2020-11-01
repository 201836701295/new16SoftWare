//
// File: dspmath_initialize.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "dspmath_initialize.h"
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
void dspmath_initialize() {
    rt_InitInfAndNaN();
    isInitialized_dspmath = true;
}

//
// File trailer for dspmath_initialize.cpp
//
// [EOF]
//
