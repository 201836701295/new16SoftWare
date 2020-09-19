//
// File: welch.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 16-Sep-2020 10:10:07
//

// Include Files
#include "welch.h"
#include "FFTImplementationCallback.h"
#include "computepsd.h"
#include "fft.h"
#include "gencoswin.h"
#include "ifft.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "psdfreqvec.h"
#include "rt_nonfinite.h"
#include <cmath>
#include <math.h>
#include <string.h>

// Function Declarations
static int div_s32(int numerator, int denominator);
static int div_s32_floor(int numerator, int denominator);

// Function Definitions

//
// Arguments    : int numerator
//                int denominator
// Return Type  : int
//
static int div_s32(int numerator, int denominator)
{
  int quotient;
  unsigned int b_numerator;
  if (denominator == 0) {
    if (numerator >= 0) {
      quotient = MAX_int32_T;
    } else {
      quotient = MIN_int32_T;
    }
  } else {
    unsigned int b_denominator;
    if (numerator < 0) {
      b_numerator = ~static_cast<unsigned int>(numerator) + 1U;
    } else {
      b_numerator = static_cast<unsigned int>(numerator);
    }

    if (denominator < 0) {
      b_denominator = ~static_cast<unsigned int>(denominator) + 1U;
    } else {
      b_denominator = static_cast<unsigned int>(denominator);
    }

    b_numerator /= b_denominator;
    if ((numerator < 0) != (denominator < 0)) {
      quotient = -static_cast<int>(b_numerator);
    } else {
      quotient = static_cast<int>(b_numerator);
    }
  }

  return quotient;
}

//
// Arguments    : int numerator
//                int denominator
// Return Type  : int
//
static int div_s32_floor(int numerator, int denominator)
{
  int quotient;
  unsigned int absNumerator;
  if (denominator == 0) {
    if (numerator >= 0) {
      quotient = MAX_int32_T;
    } else {
      quotient = MIN_int32_T;
    }
  } else {
    unsigned int absDenominator;
    boolean_T quotientNeedsNegation;
    unsigned int tempAbsQuotient;
    if (numerator < 0) {
      absNumerator = ~static_cast<unsigned int>(numerator) + 1U;
    } else {
      absNumerator = static_cast<unsigned int>(numerator);
    }

    if (denominator < 0) {
      absDenominator = ~static_cast<unsigned int>(denominator) + 1U;
    } else {
      absDenominator = static_cast<unsigned int>(denominator);
    }

    quotientNeedsNegation = ((numerator < 0) != (denominator < 0));
    tempAbsQuotient = absNumerator / absDenominator;
    if (quotientNeedsNegation) {
      absNumerator %= absDenominator;
      if (absNumerator > 0U) {
        tempAbsQuotient++;
      }

      quotient = -static_cast<int>(tempAbsQuotient);
    } else {
      quotient = static_cast<int>(tempAbsQuotient);
    }
  }

  return quotient;
}

//
// Arguments    : const coder::array<float, 2U> *x
//                int varargin_3
//                int varargin_4
//                coder::array<float, 2U> *varargout_1
//                coder::array<float, 1U> *varargout_2
// Return Type  : void
//
void welch(const coder::array<float, 2U> &x, int varargin_3, int varargin_4,
           coder::array<float, 2U> &varargout_1, coder::array<float, 1U>
           &varargout_2)
{
  int acoef;
  int noverlap;
  double b_acoef;
  coder::array<double, 1U> win1;
  coder::array<double, 1U> w;
  int i;
  int loop_ub;
  int i1;
  int i2;
  int bcoef;
  int csz_idx_0;
  coder::array<float, 1U> win;
  double b;
  coder::array<double, 2U> xStart;
  coder::array<double, 2U> xEnd;
  coder::array<float, 2U> Sxx1;
  static const char options_range[8] = { 'o', 'n', 'e', 's', 'i', 'd', 'e', 'd'
  };

  char units_data[10];
  int units_size[2];
  coder::array<float, 1U> xw;
  coder::array<float, 1U> b_xw;
  coder::array<float, 2U> wrappedData;
  coder::array<creal32_T, 1U> Xx;
  float b_win;
  coder::array<float, 1U> Sxxk;
  float Xx_re;
  acoef = static_cast<int>(std::floor(static_cast<double>(x.size(1)) / 4.5));
  noverlap = static_cast<int>(std::floor(0.5 * static_cast<double>(acoef)));
  if (acoef == 0) {
    b_acoef = 0.0;
  } else {
    b_acoef = std::fmod(static_cast<double>(acoef), 2.0);
  }

  if (b_acoef == 0.0) {
    calc_window(static_cast<double>(acoef) / 2.0, static_cast<double>(acoef),
                win1);
    w.set_size(((win1.size(0) + div_s32_floor(1 - win1.size(0), -1)) + 1));
    loop_ub = win1.size(0);
    for (i = 0; i < loop_ub; i++) {
      w[i] = win1[i];
    }

    loop_ub = div_s32_floor(1 - win1.size(0), -1);
    for (i = 0; i <= loop_ub; i++) {
      w[i + win1.size(0)] = win1[(win1.size(0) - i) - 1];
    }

    win1.set_size(w.size(0));
    loop_ub = w.size(0);
    for (i = 0; i < loop_ub; i++) {
      win1[i] = w[i];
    }
  } else {
    calc_window((static_cast<double>(acoef) + 1.0) / 2.0, static_cast<double>
                (acoef), win1);
    if (1 > win1.size(0) - 1) {
      i = 0;
      i1 = 1;
      i2 = -1;
    } else {
      i = win1.size(0) - 2;
      i1 = -1;
      i2 = 0;
    }

    loop_ub = div_s32_floor(i2 - i, i1);
    w.set_size(((win1.size(0) + loop_ub) + 1));
    bcoef = win1.size(0);
    for (i2 = 0; i2 < bcoef; i2++) {
      w[i2] = win1[i2];
    }

    for (i2 = 0; i2 <= loop_ub; i2++) {
      w[i2 + win1.size(0)] = win1[i + i1 * i2];
    }

    win1.set_size(w.size(0));
    loop_ub = w.size(0);
    for (i = 0; i < loop_ub; i++) {
      win1[i] = w[i];
    }
  }

  frexp(static_cast<double>(acoef), &csz_idx_0);
  bcoef = acoef - noverlap;
  b_acoef = (static_cast<double>(x.size(1)) - static_cast<double>(noverlap)) /
    static_cast<double>(bcoef);
  if (b_acoef < 0.0) {
    b_acoef = std::ceil(b_acoef);
  } else {
    b_acoef = std::floor(b_acoef);
  }

  win.set_size(win1.size(0));
  loop_ub = win1.size(0);
  for (i = 0; i < loop_ub; i++) {
    win[i] = static_cast<float>(win1[i]);
  }

  b = b_acoef * static_cast<double>(bcoef);
  if (rtIsNaN(b)) {
    xStart.set_size(1, 1);
    xStart[0] = rtNaN;
  } else if ((bcoef == 0) || ((1.0 < b) && (bcoef < 0)) || ((b < 1.0) && (bcoef >
    0))) {
    xStart.set_size(1, 0);
  } else if (rtIsInf(b) && (1.0 == b)) {
    xStart.set_size(1, 1);
    xStart[0] = rtNaN;
  } else {
    loop_ub = static_cast<int>(std::floor((b - 1.0) / static_cast<double>(bcoef)));
    xStart.set_size(1, (loop_ub + 1));
    for (i = 0; i <= loop_ub; i++) {
      xStart[i] = static_cast<double>(bcoef) * static_cast<double>(i) + 1.0;
    }
  }

  xEnd.set_size(1, xStart.size(1));
  loop_ub = xStart.size(0) * xStart.size(1);
  for (i = 0; i < loop_ub; i++) {
    xEnd[i] = (xStart[i] + static_cast<double>(acoef)) - 1.0;
  }

  Sxx1.set_size(0, 0);
  i = static_cast<int>(b_acoef);
  for (int ii = 0; ii < i; ii++) {
    double d;
    b = xStart[ii];
    d = xEnd[ii];
    if (b > d) {
      i1 = 0;
      i2 = 0;
    } else {
      i1 = static_cast<int>(b) - 1;
      i2 = static_cast<int>(d);
    }

    loop_ub = i2 - i1;
    xw.set_size(loop_ub);
    for (i2 = 0; i2 < loop_ub; i2++) {
      xw[i2] = x[i1 + i2];
    }

    bcoef = win.size(0);
    acoef = xw.size(0);
    if (bcoef < acoef) {
      acoef = bcoef;
    }

    if (win.size(0) == 1) {
      csz_idx_0 = xw.size(0);
    } else if (xw.size(0) == 1) {
      csz_idx_0 = win.size(0);
    } else if (xw.size(0) == win.size(0)) {
      csz_idx_0 = xw.size(0);
    } else {
      csz_idx_0 = acoef;
    }

    bcoef = win.size(0);
    acoef = xw.size(0);
    if (bcoef < acoef) {
      acoef = bcoef;
    }

    if (win.size(0) == 1) {
      acoef = xw.size(0);
    } else if (xw.size(0) == 1) {
      acoef = win.size(0);
    } else {
      if (xw.size(0) == win.size(0)) {
        acoef = xw.size(0);
      }
    }

    b_xw.set_size(acoef);
    if (csz_idx_0 != 0) {
      acoef = (xw.size(0) != 1);
      bcoef = (win.size(0) != 1);
      i1 = csz_idx_0 - 1;
      for (noverlap = 0; noverlap <= i1; noverlap++) {
        b_xw[noverlap] = xw[acoef * noverlap] * win[bcoef * noverlap];
      }
    }

    xw.set_size(varargin_3);
    for (i1 = 0; i1 < varargin_3; i1++) {
      xw[i1] = 0.0F;
    }

    if (b_xw.size(0) > varargin_3) {
      if (b_xw.size(0) == 1) {
        wrappedData.set_size(1, varargin_3);
        for (i1 = 0; i1 < varargin_3; i1++) {
          wrappedData[i1] = 0.0F;
        }
      } else {
        wrappedData.set_size(varargin_3, 1);
        for (i1 = 0; i1 < varargin_3; i1++) {
          wrappedData[i1] = 0.0F;
        }
      }

      csz_idx_0 = div_s32(b_xw.size(0), varargin_3);
      bcoef = csz_idx_0 * varargin_3;
      acoef = (b_xw.size(0) - bcoef) - 1;
      for (noverlap = 0; noverlap <= acoef; noverlap++) {
        wrappedData[noverlap] = b_xw[bcoef + noverlap];
      }

      i1 = acoef + 2;
      for (noverlap = i1; noverlap <= varargin_3; noverlap++) {
        wrappedData[noverlap - 1] = 0.0F;
      }

      for (acoef = 0; acoef < csz_idx_0; acoef++) {
        bcoef = acoef * varargin_3;
        for (noverlap = 0; noverlap < varargin_3; noverlap++) {
          wrappedData[noverlap] = wrappedData[noverlap] + b_xw[bcoef + noverlap];
        }
      }

      bcoef = wrappedData.size(0) * wrappedData.size(1);
      for (i1 = 0; i1 < bcoef; i1++) {
        xw[i1] = wrappedData[i1];
      }
    } else {
      xw.set_size(b_xw.size(0));
      loop_ub = b_xw.size(0);
      for (i1 = 0; i1 < loop_ub; i1++) {
        xw[i1] = b_xw[i1];
      }
    }

    b_fft(xw, static_cast<double>(varargin_3), Xx);
    psdfreqvec(static_cast<double>(varargin_3), static_cast<double>(varargin_4),
               win1);
    b_win = 0.0F;
    loop_ub = win.size(0);
    for (i1 = 0; i1 < loop_ub; i1++) {
      b_win += win[i1] * win[i1];
    }

    Sxxk.set_size(Xx.size(0));
    loop_ub = Xx.size(0);
    for (i1 = 0; i1 < loop_ub; i1++) {
      float Xx_im;
      Xx_re = Xx[i1].re * Xx[i1].re - Xx[i1].im * -Xx[i1].im;
      Xx_im = Xx[i1].re * -Xx[i1].im + Xx[i1].im * Xx[i1].re;
      if (Xx_im == 0.0F) {
        Xx_re /= b_win;
      } else if (Xx_re == 0.0F) {
        Xx_re = 0.0F;
      } else {
        Xx_re /= b_win;
      }

      Sxxk[i1] = Xx_re;
    }

    if (ii + 1U == 1U) {
      Sxx1.set_size(varargin_3, 1);
      for (i1 = 0; i1 < varargin_3; i1++) {
        Sxx1[i1] = Sxxk[i1];
      }
    } else {
      loop_ub = Sxx1.size(0);
      Sxxk.set_size(Sxx1.size(0));
      bcoef = Sxx1.size(0);
      for (i1 = 0; i1 < bcoef; i1++) {
        Sxxk[i1] = Sxx1[i1] + Sxxk[i1];
      }

      bcoef = Sxx1.size(0);
      Sxx1.set_size(bcoef, 1);
      for (i1 = 0; i1 < loop_ub; i1++) {
        Sxx1[i1] = Sxxk[i1];
      }
    }
  }

  loop_ub = Sxx1.size(0) * Sxx1.size(1);
  for (i = 0; i < loop_ub; i++) {
    Sxx1[i] = Sxx1[i] / static_cast<float>(b_acoef);
  }

  psdfreqvec(static_cast<double>(varargin_3), static_cast<double>(varargin_4),
             win1);
  bcoef = win1.size(0);
  xStart = win1.reshape(bcoef, 1);
  computepsd(Sxx1, xStart, options_range, static_cast<double>(varargin_3),
             static_cast<double>(varargin_4), varargout_1, w, units_data,
             units_size);
  varargout_2.set_size(w.size(0));
  loop_ub = w.size(0);
  for (i = 0; i < loop_ub; i++) {
    varargout_2[i] = static_cast<float>(w[i]);
  }
}

//
// File trailer for welch.cpp
//
// [EOF]
//
