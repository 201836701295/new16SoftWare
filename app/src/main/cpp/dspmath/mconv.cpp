//
// File: mconv.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 16-Sep-2020 10:10:07
//

// Include Files
#include "mconv.h"
#include "dspmath_data.h"
#include "dspmath_initialize.h"
#include "fft.h"
#include "ifft.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include <string.h>

// Function Definitions

//
// Arguments    : const coder::array<float, 2U> *a
//                const coder::array<float, 2U> *b
//                int n
//                coder::array<creal32_T, 2U> *c
// Return Type  : void
//
void mconv(const coder::array<float, 2U> &a, const coder::array<float, 2U> &b,
           int n, coder::array<creal32_T, 2U> &c)
{
  coder::array<creal32_T, 2U> Fa;
  coder::array<creal32_T, 2U> Fb;
  coder::array<creal32_T, 2U> b_Fa;
  int loop_ub;
  if (!isInitialized_dspmath) {
    dspmath_initialize();
  }

  fft(a, n, Fa);
  fft(b, n, Fb);
  b_Fa.set_size(1, Fa.size(1));
  loop_ub = Fa.size(0) * Fa.size(1);
  for (int i = 0; i < loop_ub; i++) {
    b_Fa[i].re = Fa[i].re * Fb[i].re - Fa[i].im * Fb[i].im;
    b_Fa[i].im = Fa[i].re * Fb[i].im + Fa[i].im * Fb[i].re;
  }

  ifft(b_Fa, n, c);
}

//
// File trailer for mconv.cpp
//
// [EOF]
//
