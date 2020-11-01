//
// File: fft.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "fft.h"
#include "FFTImplementationCallback.h"
#include "aweight.h"
#include "cweight.h"
#include "ifft.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "zweight.h"
#include <cmath>
#include <string.h>

// Function Definitions

//
// Arguments    : const coder::array<float, 1U> *x
//                double varargin_1
//                coder::array<creal32_T, 1U> *y
// Return Type  : void
//
void b_fft(const coder::array<float, 1U> &x, double varargin_1, coder::array<
        creal32_T, 1U> &y) {
    boolean_T guard1 = false;
    int loop_ub;
    int N2blue;
    int nd2;
    coder::array<float, 2U> costab1q;
    coder::array<float, 2U> costab;
    coder::array<float, 2U> sintab;
    coder::array<float, 2U> sintabinv;
    guard1 = false;
    if (x.size(0) == 0) {
        guard1 = true;
    } else {
        loop_ub = static_cast<int>(varargin_1);
        if (0 == loop_ub) {
            guard1 = true;
        } else {
            boolean_T useRadix2;
            float e;
            int n;
            int k;
            int n2;
            useRadix2 = ((loop_ub > 0) && ((loop_ub & (loop_ub - 1)) == 0));
            FFTImplementationCallback::get_algo_sizes((static_cast<int>(varargin_1)),
                                                      (useRadix2), (&N2blue), (&nd2));
            e = 6.28318548F / static_cast<float>(nd2);
            n = nd2 / 2 / 2;
            costab1q.set_size(1, (n + 1));
            costab1q[0] = 1.0F;
            nd2 = n / 2 - 1;
            for (k = 0; k <= nd2; k++) {
                costab1q[k + 1] = std::cos(e * static_cast<float>(k + 1));
            }

            nd2 += 2;
            n2 = n - 1;
            for (k = nd2; k <= n2; k++) {
                costab1q[k] = std::sin(e * static_cast<float>(n - k));
            }

            costab1q[n] = 0.0F;
            if (!useRadix2) {
                n = costab1q.size(1) - 1;
                n2 = (costab1q.size(1) - 1) << 1;
                costab.set_size(1, (n2 + 1));
                sintab.set_size(1, (n2 + 1));
                costab[0] = 1.0F;
                sintab[0] = 0.0F;
                sintabinv.set_size(1, (n2 + 1));
                for (k = 0; k < n; k++) {
                    sintabinv[k + 1] = costab1q[(n - k) - 1];
                }

                nd2 = costab1q.size(1);
                for (k = nd2; k <= n2; k++) {
                    sintabinv[k] = costab1q[k - n];
                }

                for (k = 0; k < n; k++) {
                    costab[k + 1] = costab1q[k + 1];
                    sintab[k + 1] = -costab1q[(n - k) - 1];
                }

                nd2 = costab1q.size(1);
                for (k = nd2; k <= n2; k++) {
                    costab[k] = -costab1q[n2 - k];
                    sintab[k] = -costab1q[k - n];
                }
            } else {
                n = costab1q.size(1) - 1;
                n2 = (costab1q.size(1) - 1) << 1;
                costab.set_size(1, (n2 + 1));
                sintab.set_size(1, (n2 + 1));
                costab[0] = 1.0F;
                sintab[0] = 0.0F;
                for (k = 0; k < n; k++) {
                    costab[k + 1] = costab1q[k + 1];
                    sintab[k + 1] = -costab1q[(n - k) - 1];
                }

                nd2 = costab1q.size(1);
                for (k = nd2; k <= n2; k++) {
                    costab[k] = -costab1q[n2 - k];
                    sintab[k] = -costab1q[k - n];
                }

                sintabinv.set_size(1, 0);
            }

            if (useRadix2) {
                y.set_size(loop_ub);
                if (loop_ub > x.size(0)) {
                    y.set_size(loop_ub);
                    for (nd2 = 0; nd2 < loop_ub; nd2++) {
                        y[nd2].re = 0.0F;
                        y[nd2].im = 0.0F;
                    }
                }

                if (loop_ub != 1) {
                    FFTImplementationCallback::doHalfLengthRadix2((x), (y), (static_cast<
                            int>(varargin_1)), (costab), (sintab));
                } else {
                    int b_loop_ub;
                    nd2 = x.size(0);
                    if (nd2 >= 1) {
                        nd2 = 1;
                    }

                    b_loop_ub = nd2 - 2;
                    n2 = 0;
                    int exitg1;
                    do {
                        if (n2 <= b_loop_ub) {
                            y[0].re = x[0];
                            y[0].im = 0.0F;
                            exitg1 = 1;
                        } else {
                            y[0].re = x[0];
                            y[0].im = 0.0F;
                            exitg1 = 1;
                        }
                    } while (exitg1 == 0);
                }
            } else {
                FFTImplementationCallback::dobluesteinfft((x), (N2blue), (static_cast<
                        int>(varargin_1)), (costab), (sintab), (sintabinv), (y));
            }
        }
    }

    if (guard1) {
        loop_ub = static_cast<int>(varargin_1);
        y.set_size(loop_ub);
        for (nd2 = 0; nd2 < loop_ub; nd2++) {
            y[nd2].re = 0.0F;
            y[nd2].im = 0.0F;
        }
    }
}

//
// Arguments    : const coder::array<float, 2U> *x
//                int varargin_1
//                coder::array<creal32_T, 2U> *y
// Return Type  : void
//
void fft(const coder::array<float, 2U> &x, int varargin_1, coder::array<
        creal32_T, 2U> &y) {
    int N2blue;
    int nd2;
    coder::array<float, 2U> costab1q;
    coder::array<float, 2U> costab;
    coder::array<float, 2U> sintab;
    coder::array<float, 2U> sintabinv;
    coder::array<creal32_T, 1U> yCol;
    if ((x.size(1) == 0) || (0 == varargin_1)) {
        y.set_size(1, varargin_1);
        for (int i = 0; i < varargin_1; i++) {
            y[i].re = 0.0F;
            y[i].im = 0.0F;
        }
    } else {
        boolean_T useRadix2;
        int i;
        float e;
        int n;
        int k;
        boolean_T guard1 = false;
        useRadix2 = ((varargin_1 > 0) && ((varargin_1 & (varargin_1 - 1)) == 0));
        FFTImplementationCallback::get_algo_sizes((varargin_1), (useRadix2),
                                                  (&N2blue), (&nd2));
        e = 6.28318548F / static_cast<float>(nd2);
        n = nd2 / 2 / 2;
        costab1q.set_size(1, (n + 1));
        costab1q[0] = 1.0F;
        nd2 = n / 2 - 1;
        for (k = 0; k <= nd2; k++) {
            costab1q[k + 1] = std::cos(e * static_cast<float>(k + 1));
        }

        i = nd2 + 2;
        nd2 = n - 1;
        for (k = i; k <= nd2; k++) {
            costab1q[k] = std::sin(e * static_cast<float>(n - k));
        }

        costab1q[n] = 0.0F;
        if (!useRadix2) {
            n = costab1q.size(1) - 1;
            nd2 = (costab1q.size(1) - 1) << 1;
            costab.set_size(1, (nd2 + 1));
            sintab.set_size(1, (nd2 + 1));
            costab[0] = 1.0F;
            sintab[0] = 0.0F;
            sintabinv.set_size(1, (nd2 + 1));
            for (k = 0; k < n; k++) {
                sintabinv[k + 1] = costab1q[(n - k) - 1];
            }

            i = costab1q.size(1);
            for (k = i; k <= nd2; k++) {
                sintabinv[k] = costab1q[k - n];
            }

            for (k = 0; k < n; k++) {
                costab[k + 1] = costab1q[k + 1];
                sintab[k + 1] = -costab1q[(n - k) - 1];
            }

            i = costab1q.size(1);
            for (k = i; k <= nd2; k++) {
                costab[k] = -costab1q[nd2 - k];
                sintab[k] = -costab1q[k - n];
            }
        } else {
            n = costab1q.size(1) - 1;
            nd2 = (costab1q.size(1) - 1) << 1;
            costab.set_size(1, (nd2 + 1));
            sintab.set_size(1, (nd2 + 1));
            costab[0] = 1.0F;
            sintab[0] = 0.0F;
            for (k = 0; k < n; k++) {
                costab[k + 1] = costab1q[k + 1];
                sintab[k + 1] = -costab1q[(n - k) - 1];
            }

            i = costab1q.size(1);
            for (k = i; k <= nd2; k++) {
                costab[k] = -costab1q[nd2 - k];
                sintab[k] = -costab1q[k - n];
            }

            sintabinv.set_size(1, 0);
        }

        guard1 = false;
        if (useRadix2) {
            yCol.set_size(varargin_1);
            if (varargin_1 > x.size(1)) {
                yCol.set_size(varargin_1);
                for (i = 0; i < varargin_1; i++) {
                    yCol[i].re = 0.0F;
                    yCol[i].im = 0.0F;
                }
            }

            if (varargin_1 != 1) {
                coder::array<float, 1U> b_x;
                nd2 = x.size(1);
                b_x = x.reshape(nd2);
                FFTImplementationCallback::doHalfLengthRadix2((b_x), (yCol), (varargin_1),
                                                              (costab), (sintab));
                guard1 = true;
            } else {
                int loop_ub;
                nd2 = x.size(1);
                if (nd2 >= 1) {
                    nd2 = 1;
                }

                loop_ub = nd2 - 2;
                n = 0;
                int exitg1;
                do {
                    if (n <= loop_ub) {
                        yCol[0].re = x[0];
                        yCol[0].im = 0.0F;
                        exitg1 = 1;
                    } else {
                        yCol[0].re = x[0];
                        yCol[0].im = 0.0F;
                        guard1 = true;
                        exitg1 = 1;
                    }
                } while (exitg1 == 0);
            }
        } else {
            coder::array<float, 1U> b_x;
            nd2 = x.size(1);
            b_x = x.reshape(nd2);
            FFTImplementationCallback::dobluesteinfft((b_x), (N2blue), (varargin_1),
                                                      (costab), (sintab), (sintabinv), (yCol));
            guard1 = true;
        }

        if (guard1) {
            y.set_size(1, varargin_1);
            for (i = 0; i < varargin_1; i++) {
                y[i] = yCol[i];
            }
        }
    }
}

//
// File trailer for fft.cpp
//
// [EOF]
//
