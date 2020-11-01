//
// File: computeperiodogram.h
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//
#ifndef COMPUTEPERIODOGRAM_H
#define COMPUTEPERIODOGRAM_H

// Include Files
#include <cstddef>
#include <cstdlib>
#include "rtwtypes.h"
#include "dspmath_types.h"

// Function Declarations
extern void computeperiodogram(const coder::array<float, 1U> &x, const coder::
array<float, 1U> &win, double nfft, double Fs, coder::array<float, 1U> &Pxx,
                               coder::array<double, 1U> &F);

#endif

//
// File trailer for computeperiodogram.h
//
// [EOF]
//
