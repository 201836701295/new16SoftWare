//
// File: fft.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 28-Sep-2020 16:47:53
//

// Include Files
#include "fft.h"
#include "FFTImplementationCallback.h"
#include "ifft.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mslm.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include <cmath>
#include <string.h>

// Function Definitions

//
// Arguments    : const coder::array<float, 2U> *x
//                coder::array<creal32_T, 2U> *y
// Return Type  : void
//
void b_fft(const coder::array<float, 2U> &x, coder::array<creal32_T, 2U> &y) {
    int iDelta2;
    int nd2;
    float twid_re;
    int iheight;
    int ihi;
    coder::array<float, 2U> costab1q;
    int k;
    coder::array<float, 2U> costab;
    coder::array<float, 2U> sintab;
    coder::array<float, 2U> sintabinv;
    coder::array<creal32_T, 1U> yCol;
    int ju;
    int i;
    float twid_im;
    if (x.size(1) == 0) {
        y.set_size(1, x.size(1));
        nd2 = x.size(1);
        for (iheight = 0; iheight < nd2; iheight++) {
            y[iheight].re = 0.0F;
            y[iheight].im = 0.0F;
        }
    } else {
        boolean_T useRadix2;
        useRadix2 = ((x.size(1) & (x.size(1) - 1)) == 0);
        FFTImplementationCallback::get_algo_sizes((x.size(1)), (useRadix2),
                                                  (&iDelta2), (&nd2));
        twid_re = 6.28318548F / static_cast<float>(nd2);
        ihi = nd2 / 2 / 2;
        costab1q.set_size(1, (ihi + 1));
        costab1q[0] = 1.0F;
        nd2 = ihi / 2 - 1;
        for (k = 0; k <= nd2; k++) {
            costab1q[k + 1] = std::cos(twid_re * static_cast<float>(k + 1));
        }

        iheight = nd2 + 2;
        nd2 = ihi - 1;
        for (k = iheight; k <= nd2; k++) {
            costab1q[k] = std::sin(twid_re * static_cast<float>(ihi - k));
        }

        costab1q[ihi] = 0.0F;
        if (!useRadix2) {
            ihi = costab1q.size(1) - 1;
            nd2 = (costab1q.size(1) - 1) << 1;
            costab.set_size(1, (nd2 + 1));
            sintab.set_size(1, (nd2 + 1));
            costab[0] = 1.0F;
            sintab[0] = 0.0F;
            sintabinv.set_size(1, (nd2 + 1));
            for (k = 0; k < ihi; k++) {
                sintabinv[k + 1] = costab1q[(ihi - k) - 1];
            }

            iheight = costab1q.size(1);
            for (k = iheight; k <= nd2; k++) {
                sintabinv[k] = costab1q[k - ihi];
            }

            for (k = 0; k < ihi; k++) {
                costab[k + 1] = costab1q[k + 1];
                sintab[k + 1] = -costab1q[(ihi - k) - 1];
            }

            iheight = costab1q.size(1);
            for (k = iheight; k <= nd2; k++) {
                costab[k] = -costab1q[nd2 - k];
                sintab[k] = -costab1q[k - ihi];
            }
        } else {
            ihi = costab1q.size(1) - 1;
            nd2 = (costab1q.size(1) - 1) << 1;
            costab.set_size(1, (nd2 + 1));
            sintab.set_size(1, (nd2 + 1));
            costab[0] = 1.0F;
            sintab[0] = 0.0F;
            for (k = 0; k < ihi; k++) {
                costab[k + 1] = costab1q[k + 1];
                sintab[k + 1] = -costab1q[(ihi - k) - 1];
            }

            iheight = costab1q.size(1);
            for (k = iheight; k <= nd2; k++) {
                costab[k] = -costab1q[nd2 - k];
                sintab[k] = -costab1q[k - ihi];
            }

            sintabinv.set_size(1, 0);
        }

        if (useRadix2) {
            yCol.set_size(x.size(1));
            if (x.size(1) != 1) {
                coder::array<float, 1U> b_x;
                nd2 = x.size(1);
                b_x = x.reshape(nd2);
                FFTImplementationCallback::doHalfLengthRadix2((b_x), (yCol), (x.size(1)),
                                                              (costab), (sintab));
            } else {
                int nRowsD2;
                int ix;
                float temp_re;
                float temp_im;
                iDelta2 = x.size(1) - 2;
                iheight = x.size(1) - 2;
                nRowsD2 = x.size(1) / 2;
                k = nRowsD2 / 2;
                ix = 0;
                nd2 = 0;
                ju = 0;
                for (i = 0; i <= iDelta2; i++) {
                    yCol[nd2].re = x[ix];
                    yCol[nd2].im = 0.0F;
                    ihi = x.size(1);
                    useRadix2 = true;
                    while (useRadix2) {
                        ihi >>= 1;
                        ju ^= ihi;
                        useRadix2 = ((ju & ihi) == 0);
                    }

                    nd2 = ju;
                    ix++;
                }

                yCol[nd2].re = x[ix];
                yCol[nd2].im = 0.0F;
                if (x.size(1) > 1) {
                    for (i = 0; i <= iheight; i += 2) {
                        temp_re = yCol[i + 1].re;
                        temp_im = yCol[i + 1].im;
                        twid_re = yCol[i].re;
                        twid_im = yCol[i].im;
                        yCol[i + 1].re = yCol[i].re - yCol[i + 1].re;
                        yCol[i + 1].im = yCol[i].im - yCol[i + 1].im;
                        twid_re += temp_re;
                        twid_im += temp_im;
                        yCol[i].re = twid_re;
                        yCol[i].im = twid_im;
                    }
                }

                nd2 = 2;
                iDelta2 = 4;
                iheight = ((k - 1) << 2) + 1;
                while (k > 0) {
                    int temp_re_tmp;
                    for (i = 0; i < iheight; i += iDelta2) {
                        temp_re_tmp = i + nd2;
                        temp_re = yCol[temp_re_tmp].re;
                        temp_im = yCol[temp_re_tmp].im;
                        yCol[temp_re_tmp].re = yCol[i].re - yCol[temp_re_tmp].re;
                        yCol[temp_re_tmp].im = yCol[i].im - yCol[temp_re_tmp].im;
                        yCol[i].re = yCol[i].re + temp_re;
                        yCol[i].im = yCol[i].im + temp_im;
                    }

                    ix = 1;
                    for (ju = k; ju < nRowsD2; ju += k) {
                        twid_re = costab[ju];
                        twid_im = sintab[ju];
                        i = ix;
                        ihi = ix + iheight;
                        while (i < ihi) {
                            temp_re_tmp = i + nd2;
                            temp_re = twid_re * yCol[temp_re_tmp].re - twid_im *
                                                                       yCol[temp_re_tmp].im;
                            temp_im = twid_re * yCol[temp_re_tmp].im + twid_im *
                                                                       yCol[temp_re_tmp].re;
                            yCol[temp_re_tmp].re = yCol[i].re - temp_re;
                            yCol[temp_re_tmp].im = yCol[i].im - temp_im;
                            yCol[i].re = yCol[i].re + temp_re;
                            yCol[i].im = yCol[i].im + temp_im;
                            i += iDelta2;
                        }

                        ix++;
                    }

                    k /= 2;
                    nd2 = iDelta2;
                    iDelta2 += iDelta2;
                    iheight -= nd2;
                }
            }
        } else {
            coder::array<float, 1U> b_x;
            nd2 = x.size(1);
            b_x = x.reshape(nd2);
            FFTImplementationCallback::dobluesteinfft((b_x), (iDelta2), (x.size(1)),
                                                      (costab), (sintab), (sintabinv), (yCol));
        }

        y.set_size(1, x.size(1));
        nd2 = x.size(1);
        for (iheight = 0; iheight < nd2; iheight++) {
            y[iheight] = yCol[iheight];
        }
    }
}

//
// Arguments    : const coder::array<float, 1U> *x
//                double varargin_1
//                coder::array<creal32_T, 1U> *y
// Return Type  : void
//
void c_fft(const coder::array<float, 1U> &x, double varargin_1, coder::array<
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
