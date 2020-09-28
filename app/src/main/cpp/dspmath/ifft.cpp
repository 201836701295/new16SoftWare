//
// File: ifft.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 28-Sep-2020 16:47:53
//

// Include Files
#include "ifft.h"
#include "FFTImplementationCallback.h"
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
// Arguments    : const coder::array<creal32_T, 2U> *x
//                int varargin_1
//                coder::array<creal32_T, 2U> *y
// Return Type  : void
//
void ifft(const coder::array<creal32_T, 2U> &x, int varargin_1, coder::array<
        creal32_T, 2U> &y) {
    int N2blue;
    int nd2;
    int rt;
    coder::array<float, 2U> costab1q;
    coder::array<float, 2U> costab;
    coder::array<float, 2U> sintab;
    coder::array<float, 2U> sintabinv;
    coder::array<creal32_T, 1U> wwc;
    coder::array<creal32_T, 1U> yCol;
    coder::array<creal32_T, 1U> fy;
    coder::array<creal32_T, 1U> fv;
    if ((x.size(1) == 0) || (0 == varargin_1)) {
        y.set_size(1, varargin_1);
        for (int nInt2 = 0; nInt2 < varargin_1; nInt2++) {
            y[nInt2].re = 0.0F;
            y[nInt2].im = 0.0F;
        }
    } else {
        boolean_T useRadix2;
        int nInt2;
        float nt_re;
        int k;
        useRadix2 = ((varargin_1 > 0) && ((varargin_1 & (varargin_1 - 1)) == 0));
        FFTImplementationCallback::get_algo_sizes((varargin_1), (useRadix2),
                                                  (&N2blue), (&nd2));
        nt_re = 6.28318548F / static_cast<float>(nd2);
        rt = nd2 / 2 / 2;
        costab1q.set_size(1, (rt + 1));
        costab1q[0] = 1.0F;
        nd2 = rt / 2 - 1;
        for (k = 0; k <= nd2; k++) {
            costab1q[k + 1] = std::cos(nt_re * static_cast<float>(k + 1));
        }

        nInt2 = nd2 + 2;
        nd2 = rt - 1;
        for (k = nInt2; k <= nd2; k++) {
            costab1q[k] = std::sin(nt_re * static_cast<float>(rt - k));
        }

        costab1q[rt] = 0.0F;
        if (!useRadix2) {
            rt = costab1q.size(1) - 1;
            nd2 = (costab1q.size(1) - 1) << 1;
            costab.set_size(1, (nd2 + 1));
            sintab.set_size(1, (nd2 + 1));
            costab[0] = 1.0F;
            sintab[0] = 0.0F;
            sintabinv.set_size(1, (nd2 + 1));
            for (k = 0; k < rt; k++) {
                sintabinv[k + 1] = costab1q[(rt - k) - 1];
            }

            nInt2 = costab1q.size(1);
            for (k = nInt2; k <= nd2; k++) {
                sintabinv[k] = costab1q[k - rt];
            }

            for (k = 0; k < rt; k++) {
                costab[k + 1] = costab1q[k + 1];
                sintab[k + 1] = -costab1q[(rt - k) - 1];
            }

            nInt2 = costab1q.size(1);
            for (k = nInt2; k <= nd2; k++) {
                costab[k] = -costab1q[nd2 - k];
                sintab[k] = -costab1q[k - rt];
            }
        } else {
            rt = costab1q.size(1) - 1;
            nd2 = (costab1q.size(1) - 1) << 1;
            costab.set_size(1, (nd2 + 1));
            sintab.set_size(1, (nd2 + 1));
            costab[0] = 1.0F;
            sintab[0] = 0.0F;
            for (k = 0; k < rt; k++) {
                costab[k + 1] = costab1q[k + 1];
                sintab[k + 1] = costab1q[(rt - k) - 1];
            }

            nInt2 = costab1q.size(1);
            for (k = nInt2; k <= nd2; k++) {
                costab[k] = -costab1q[nd2 - k];
                sintab[k] = costab1q[k - rt];
            }

            sintabinv.set_size(1, 0);
        }

        if (useRadix2) {
            nd2 = x.size(1);
            wwc = x.reshape(nd2);
            FFTImplementationCallback::r2br_r2dit_trig_impl((wwc), (varargin_1),
                                                            (costab), (sintab), (yCol));
            if (yCol.size(0) > 1) {
                nt_re = 1.0F / static_cast<float>(yCol.size(0));
                nd2 = yCol.size(0);
                for (nInt2 = 0; nInt2 < nd2; nInt2++) {
                    yCol[nInt2].re = nt_re * yCol[nInt2].re;
                    yCol[nInt2].im = nt_re * yCol[nInt2].im;
                }
            }
        } else {
            int idx;
            float nt_im;
            nd2 = (varargin_1 + varargin_1) - 1;
            wwc.set_size(nd2);
            idx = varargin_1;
            rt = 0;
            wwc[varargin_1 - 1].re = 1.0F;
            wwc[varargin_1 - 1].im = 0.0F;
            nInt2 = varargin_1 << 1;
            for (k = 0; k <= varargin_1 - 2; k++) {
                int b_y;
                b_y = ((k + 1) << 1) - 1;
                if (nInt2 - rt <= b_y) {
                    rt += b_y - nInt2;
                } else {
                    rt += b_y;
                }

                nt_im = 3.14159274F * static_cast<float>(rt) / static_cast<float>
                (varargin_1);
                if (nt_im == 0.0F) {
                    nt_re = 1.0F;
                    nt_im = 0.0F;
                } else {
                    nt_re = std::cos(nt_im);
                    nt_im = std::sin(nt_im);
                }

                wwc[idx - 2].re = nt_re;
                wwc[idx - 2].im = -nt_im;
                idx--;
            }

            idx = 0;
            nInt2 = nd2 - 1;
            for (k = nInt2; k >= varargin_1; k--) {
                wwc[k] = wwc[idx];
                idx++;
            }

            yCol.set_size(varargin_1);
            if (varargin_1 > x.size(1)) {
                yCol.set_size(varargin_1);
                for (nInt2 = 0; nInt2 < varargin_1; nInt2++) {
                    yCol[nInt2].re = 0.0F;
                    yCol[nInt2].im = 0.0F;
                }
            }

            nd2 = x.size(1);
            if (varargin_1 < nd2) {
                nd2 = varargin_1;
            }

            rt = 0;
            for (k = 0; k < nd2; k++) {
                nInt2 = (varargin_1 + k) - 1;
                yCol[k].re = wwc[nInt2].re * x[rt].re + wwc[nInt2].im * x[rt].im;
                yCol[k].im = wwc[nInt2].re * x[rt].im - wwc[nInt2].im * x[rt].re;
                rt++;
            }

            nInt2 = nd2 + 1;
            for (k = nInt2; k <= varargin_1; k++) {
                yCol[k - 1].re = 0.0F;
                yCol[k - 1].im = 0.0F;
            }

            FFTImplementationCallback::r2br_r2dit_trig_impl((yCol), (N2blue), (costab),
                                                            (sintab), (fy));
            FFTImplementationCallback::r2br_r2dit_trig_impl((wwc), (N2blue), (costab),
                                                            (sintab), (fv));
            nd2 = fy.size(0);
            for (nInt2 = 0; nInt2 < nd2; nInt2++) {
                nt_re = fy[nInt2].re * fv[nInt2].im + fy[nInt2].im * fv[nInt2].re;
                fy[nInt2].re = fy[nInt2].re * fv[nInt2].re - fy[nInt2].im * fv[nInt2].im;
                fy[nInt2].im = nt_re;
            }

            FFTImplementationCallback::r2br_r2dit_trig_impl((fy), (N2blue), (costab),
                                                            (sintabinv), (fv));
            if (fv.size(0) > 1) {
                nt_re = 1.0F / static_cast<float>(fv.size(0));
                nd2 = fv.size(0);
                for (nInt2 = 0; nInt2 < nd2; nInt2++) {
                    fv[nInt2].re = nt_re * fv[nInt2].re;
                    fv[nInt2].im = nt_re * fv[nInt2].im;
                }
            }

            idx = 0;
            nInt2 = static_cast<int>(static_cast<float>(varargin_1));
            nd2 = wwc.size(0);
            for (k = nInt2; k <= nd2; k++) {
                yCol[idx].re = wwc[k - 1].re * fv[k - 1].re + wwc[k - 1].im * fv[k - 1].
                        im;
                yCol[idx].im = wwc[k - 1].re * fv[k - 1].im - wwc[k - 1].im * fv[k - 1].
                        re;
                if (yCol[idx].im == 0.0F) {
                    nt_im = yCol[idx].re / static_cast<float>(varargin_1);
                    nt_re = 0.0F;
                } else if (yCol[idx].re == 0.0F) {
                    nt_im = 0.0F;
                    nt_re = yCol[idx].im / static_cast<float>(varargin_1);
                } else {
                    nt_im = yCol[idx].re / static_cast<float>(varargin_1);
                    nt_re = yCol[idx].im / static_cast<float>(varargin_1);
                }

                yCol[idx].re = nt_im;
                yCol[idx].im = nt_re;
                idx++;
            }
        }

        y.set_size(1, varargin_1);
        for (nInt2 = 0; nInt2 < varargin_1; nInt2++) {
            y[nInt2] = yCol[nInt2];
        }
    }
}

//
// File trailer for ifft.cpp
//
// [EOF]
//
