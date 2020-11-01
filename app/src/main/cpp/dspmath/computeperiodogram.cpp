//
// File: computeperiodogram.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "computeperiodogram.h"
#include "FFTImplementationCallback.h"
#include "aweight.h"
#include "cweight.h"
#include "fft.h"
#include "ifft.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "psdfreqvec.h"
#include "rt_nonfinite.h"
#include "welch.h"
#include "zweight.h"
#include <string.h>

// Function Declarations
static int div_s32(int numerator, int denominator);

// Function Definitions

//
// Arguments    : int numerator
//                int denominator
// Return Type  : int
//
static int div_s32(int numerator, int denominator) {
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
// Arguments    : const coder::array<float, 1U> *x
//                const coder::array<float, 1U> *win
//                double nfft
//                double Fs
//                coder::array<float, 1U> *Pxx
//                coder::array<double, 1U> *F
// Return Type  : void
//
void computeperiodogram(const coder::array<float, 1U> &x, const coder::array<
        float, 1U> &win, double nfft, double Fs, coder::array<float, 1U> &Pxx, coder::
                        array<double, 1U> &F) {
    int acoef;
    int bcoef;
    int csz_idx_0;
    coder::array<float, 1U> xw;
    coder::array<float, 1U> b_xw;
    int k;
    coder::array<float, 2U> wrappedData;
    coder::array<creal32_T, 1U> Xx;
    float b_win;
    float Xx_re;
    if (rtIsNaN(Fs)) {
        Fs = 6.2831853071795862;
    }

    acoef = win.size(0);
    bcoef = x.size(0);
    if (acoef < bcoef) {
        bcoef = acoef;
    }

    if (win.size(0) == 1) {
        csz_idx_0 = x.size(0);
    } else if (x.size(0) == 1) {
        csz_idx_0 = win.size(0);
    } else if (x.size(0) == win.size(0)) {
        csz_idx_0 = x.size(0);
    } else {
        csz_idx_0 = bcoef;
    }

    acoef = win.size(0);
    bcoef = x.size(0);
    if (acoef < bcoef) {
        bcoef = acoef;
    }

    if (win.size(0) == 1) {
        bcoef = x.size(0);
    } else if (x.size(0) == 1) {
        bcoef = win.size(0);
    } else {
        if (x.size(0) == win.size(0)) {
            bcoef = x.size(0);
        }
    }

    xw.set_size(bcoef);
    if (csz_idx_0 != 0) {
        acoef = (x.size(0) != 1);
        bcoef = (win.size(0) != 1);
        csz_idx_0--;
        for (k = 0; k <= csz_idx_0; k++) {
            xw[k] = x[acoef * k] * win[bcoef * k];
        }
    }

    bcoef = static_cast<int>(nfft);
    b_xw.set_size(bcoef);
    for (csz_idx_0 = 0; csz_idx_0 < bcoef; csz_idx_0++) {
        b_xw[csz_idx_0] = 0.0F;
    }

    if (xw.size(0) > nfft) {
        int nFullPasses;
        if (xw.size(0) == 1) {
            wrappedData.set_size(1, bcoef);
            for (csz_idx_0 = 0; csz_idx_0 < bcoef; csz_idx_0++) {
                wrappedData[csz_idx_0] = 0.0F;
            }
        } else {
            wrappedData.set_size(bcoef, 1);
            for (csz_idx_0 = 0; csz_idx_0 < bcoef; csz_idx_0++) {
                wrappedData[csz_idx_0] = 0.0F;
            }
        }

        nFullPasses = div_s32(xw.size(0), bcoef);
        csz_idx_0 = nFullPasses * bcoef;
        acoef = (xw.size(0) - csz_idx_0) - 1;
        for (k = 0; k <= acoef; k++) {
            wrappedData[k] = xw[csz_idx_0 + k];
        }

        csz_idx_0 = acoef + 2;
        for (k = csz_idx_0; k <= bcoef; k++) {
            wrappedData[k - 1] = 0.0F;
        }

        for (acoef = 0; acoef < nFullPasses; acoef++) {
            csz_idx_0 = acoef * bcoef;
            for (k = 0; k < bcoef; k++) {
                wrappedData[k] = wrappedData[k] + xw[csz_idx_0 + k];
            }
        }

        acoef = wrappedData.size(0) * wrappedData.size(1);
        for (csz_idx_0 = 0; csz_idx_0 < acoef; csz_idx_0++) {
            b_xw[csz_idx_0] = wrappedData[csz_idx_0];
        }
    } else {
        b_xw.set_size(xw.size(0));
        bcoef = xw.size(0);
        for (csz_idx_0 = 0; csz_idx_0 < bcoef; csz_idx_0++) {
            b_xw[csz_idx_0] = xw[csz_idx_0];
        }
    }

    b_fft(b_xw, nfft, Xx);
    psdfreqvec(nfft, Fs, F);
    b_win = 0.0F;
    bcoef = win.size(0);
    for (csz_idx_0 = 0; csz_idx_0 < bcoef; csz_idx_0++) {
        b_win += win[csz_idx_0] * win[csz_idx_0];
    }

    Pxx.set_size(Xx.size(0));
    bcoef = Xx.size(0);
    for (csz_idx_0 = 0; csz_idx_0 < bcoef; csz_idx_0++) {
        float Xx_im;
        Xx_re = Xx[csz_idx_0].re * Xx[csz_idx_0].re - Xx[csz_idx_0].im *
                                                      -Xx[csz_idx_0].im;
        Xx_im = Xx[csz_idx_0].re * -Xx[csz_idx_0].im + Xx[csz_idx_0].im *
                                                       Xx[csz_idx_0].re;
        if (Xx_im == 0.0F) {
            Xx_re /= b_win;
        } else if (Xx_re == 0.0F) {
            Xx_re = 0.0F;
        } else {
            Xx_re /= b_win;
        }

        Pxx[csz_idx_0] = Xx_re;
    }
}

//
// File trailer for computeperiodogram.cpp
//
// [EOF]
//
