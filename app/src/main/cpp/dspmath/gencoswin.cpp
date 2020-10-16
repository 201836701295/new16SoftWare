//
// File: gencoswin.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 16-Oct-2020 18:58:30
//

// Include Files
#include "gencoswin.h"
#include "computepsd.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "slmfunc.h"
#include "welch.h"
#include <cmath>
#include <string.h>

// Function Definitions

//
// Arguments    : double m
//                double n
//                coder::array<double, 1U> *w
// Return Type  : void
//
void calc_window(double m, double n, coder::array<double, 1U> &w) {
    coder::array<double, 2U> y;
    int nx;
    int k;
    if (rtIsNaN(m - 1.0)) {
        y.set_size(1, 1);
        y[0] = rtNaN;
    } else if (m - 1.0 < 0.0) {
        y.set_size(1, 0);
    } else if (rtIsInf(m - 1.0) && (0.0 == m - 1.0)) {
        y.set_size(1, 1);
    y[0] = rtNaN;
  } else {
    nx = static_cast<int>(std::floor(m - 1.0));
    y.set_size(1, (nx + 1));
    for (k = 0; k <= nx; k++) {
      y[k] = k;
    }
  }

  w.set_size(y.size(1));
  nx = y.size(1);
  for (k = 0; k < nx; k++) {
    w[k] = 6.2831853071795862 * (y[k] / (n - 1.0));
  }

  nx = w.size(0);
  for (k = 0; k < nx; k++) {
    w[k] = std::cos(w[k]);
  }

  nx = w.size(0);
  for (k = 0; k < nx; k++) {
    w[k] = 0.54 - 0.46 * w[k];
  }
}

//
// File trailer for gencoswin.cpp
//
// [EOF]
//
