//
// File: dspmath_types.h
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 15-Oct-2020 21:35:42
//
#ifndef DSPMATH_TYPES_H
#define DSPMATH_TYPES_H

// Include Files
#include "rtwtypes.h"
#include "coder_array.h"
#ifdef _MSC_VER

#pragma warning(push)
#pragma warning(disable : 4251)

#endif

// Type Definitions
class FFTImplementationCallback {
public:
    static void get_algo_sizes(int nfft, boolean_T useRadix2, int *n2blue, int
    *nRows);

    static void dobluesteinfft(const coder::array<float, 1U> &x, int n2blue, int
    nfft, const coder::array<float, 2U> &costab, const coder::array<float, 2U>
                               &sintab, const coder::array<float, 2U> &sintabinv,
                               coder::array<creal32_T,
                                       1U> &y);

    static void r2br_r2dit_trig_impl(const coder::array<creal32_T, 1U> &x, int
    unsigned_nRows, const coder::array<float, 2U> &costab, const coder::array<
            float, 2U> &sintab, coder::array<creal32_T, 1U> &y);

    static void doHalfLengthRadix2(const coder::array<float, 1U> &x, coder::array<
            creal32_T, 1U> &y, int unsigned_nRows, const coder::array<float, 2U> &costab,
                                   const coder::array<float, 2U> &sintab);

protected:
    static void doHalfLengthBluestein(const coder::array<float, 1U> &x, coder::
    array<creal32_T, 1U> &y, int nrowsx, int nRows, int nfft, const coder::array<
            creal32_T, 1U> &wwc, const coder::array<float, 2U> &costab, const coder::
    array<float, 2U> &sintab, const coder::array<float, 2U> &costabinv, const
                                      coder::array<float, 2U> &sintabinv);
};

#ifdef _MSC_VER

#pragma warning(pop)

#endif
#endif

//
// File trailer for dspmath_types.h
//
// [EOF]
//
