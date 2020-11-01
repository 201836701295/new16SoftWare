//
// File: fft.h
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//
#ifndef FFT_H
#define FFT_H

// Include Files
#include <cstddef>
#include <cstdlib>
#include "rtwtypes.h"
#include "dspmath_types.h"

// Function Declarations
extern void b_fft(const coder::array<float, 1U> &x, double varargin_1, coder::
array<creal32_T, 1U> &y);

extern void fft(const coder::array<float, 2U> &x, int varargin_1, coder::array<
        creal32_T, 2U> &y);

#endif

//
// File trailer for fft.h
//
// [EOF]
//
