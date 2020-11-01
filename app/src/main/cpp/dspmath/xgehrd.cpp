//
// File: xgehrd.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "xgehrd.h"
#include "aweight.h"
#include "cweight.h"
#include "dspmath_rtwutil.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "xdlanv2.h"
#include "xnrm2.h"
#include "zweight.h"
#include <cmath>
#include <cstring>
#include <string.h>

// Function Definitions

//
// Arguments    : double a[36]
// Return Type  : void
//
void xgehrd(double a[36]) {
    int i;
    double work[6];
    int b_i;
    double alpha1;
    double tau[5];
    double temp;
    double beta1;
    int k;
    int jA;
    for (i = 0; i < 6; i++) {
        work[i] = 0.0;
    }

    for (i = 0; i < 5; i++) {
        int in;
        int alpha1_tmp;
        int c_i;
        int coltop;
        int iv0_tmp;
        int knt;
        int lastv;
        int lastc;
        boolean_T exitg2;
        int ix;
        int exitg1;
        int d_i;
        b_i = i * 6 + 2;
        in = (i + 1) * 6;
        alpha1_tmp = (i + 6 * i) + 1;
        alpha1 = a[alpha1_tmp];
        if (i + 3 < 6) {
            c_i = i + 1;
        } else {
            c_i = 4;
        }

        coltop = c_i + b_i;
        tau[i] = 0.0;
        temp = xnrm2(4 - i, a, coltop);
        if (temp != 0.0) {
            beta1 = rt_hypotd_snf(a[alpha1_tmp], temp);
            if (a[alpha1_tmp] >= 0.0) {
                beta1 = -beta1;
            }

            if (std::abs(beta1) < 1.0020841800044864E-292) {
                knt = -1;
                c_i = (coltop - i) + 3;
                do {
                    knt++;
                    for (k = coltop; k <= c_i; k++) {
                        a[k - 1] *= 9.9792015476736E+291;
                    }

                    beta1 *= 9.9792015476736E+291;
                    alpha1 *= 9.9792015476736E+291;
                } while (!(std::abs(beta1) >= 1.0020841800044864E-292));

                beta1 = rt_hypotd_snf(alpha1, xnrm2(4 - i, a, coltop));
                if (alpha1 >= 0.0) {
                    beta1 = -beta1;
                }

                tau[i] = (beta1 - alpha1) / beta1;
                temp = 1.0 / (alpha1 - beta1);
                c_i = (coltop - i) + 3;
                for (k = coltop; k <= c_i; k++) {
                    a[k - 1] *= temp;
                }

                for (k = 0; k <= knt; k++) {
                    beta1 *= 1.0020841800044864E-292;
                }

                alpha1 = beta1;
            } else {
                tau[i] = (beta1 - a[alpha1_tmp]) / beta1;
                temp = 1.0 / (a[alpha1_tmp] - beta1);
                c_i = (coltop - i) + 3;
                for (k = coltop; k <= c_i; k++) {
                    a[k - 1] *= temp;
                }

                alpha1 = beta1;
            }
        }

        a[alpha1_tmp] = 1.0;
        iv0_tmp = (i + b_i) - 1;
        coltop = in + 1;
        if (tau[i] != 0.0) {
            lastv = 4 - i;
            b_i = (iv0_tmp - i) + 4;
            while ((lastv + 1 > 0) && (a[b_i] == 0.0)) {
                lastv--;
                b_i--;
            }

            lastc = 6;
            exitg2 = false;
            while ((!exitg2) && (lastc > 0)) {
                knt = in + lastc;
                k = knt;
                do {
                    exitg1 = 0;
                    if (k <= knt + lastv * 6) {
                        if (a[k - 1] != 0.0) {
                            exitg1 = 1;
                        } else {
                            k += 6;
                        }
                    } else {
                        lastc--;
                        exitg1 = 2;
                    }
                } while (exitg1 == 0);

                if (exitg1 == 1) {
                    exitg2 = true;
                }
            }
        } else {
            lastv = -1;
            lastc = 0;
        }

        if (lastv + 1 > 0) {
            if (lastc != 0) {
                if (0 <= lastc - 1) {
                    std::memset(&work[0], 0, lastc * sizeof(double));
                }

                ix = iv0_tmp;
                c_i = (in + 6 * lastv) + 1;
                for (b_i = coltop; b_i <= c_i; b_i += 6) {
                    knt = 0;
                    d_i = (b_i + lastc) - 1;
                    for (k = b_i; k <= d_i; k++) {
                        work[knt] += a[k - 1] * a[ix];
                        knt++;
                    }

                    ix++;
                }
            }

            if (!(-tau[i] == 0.0)) {
                jA = in;
                knt = iv0_tmp;
                for (b_i = 0; b_i <= lastv; b_i++) {
                    if (a[knt] != 0.0) {
                        temp = a[knt] * -tau[i];
                        ix = 0;
                        c_i = jA + 1;
                        d_i = lastc + jA;
                        for (coltop = c_i; coltop <= d_i; coltop++) {
                            a[coltop - 1] += work[ix] * temp;
                            ix++;
                        }
                    }

                    knt++;
                    jA += 6;
                }
            }
        }

        jA = (i + in) + 2;
        if (tau[i] != 0.0) {
            lastv = 5 - i;
            b_i = (iv0_tmp - i) + 4;
            while ((lastv > 0) && (a[b_i] == 0.0)) {
                lastv--;
                b_i--;
            }

            lastc = 4 - i;
            exitg2 = false;
            while ((!exitg2) && (lastc + 1 > 0)) {
                coltop = jA + lastc * 6;
                k = coltop;
                do {
                    exitg1 = 0;
                    if (k <= (coltop + lastv) - 1) {
                        if (a[k - 1] != 0.0) {
                            exitg1 = 1;
                        } else {
                            k++;
                        }
                    } else {
                        lastc--;
                        exitg1 = 2;
                    }
                } while (exitg1 == 0);

                if (exitg1 == 1) {
                    exitg2 = true;
                }
            }
        } else {
            lastv = 0;
            lastc = -1;
        }

        if (lastv > 0) {
            if (lastc + 1 != 0) {
                if (0 <= lastc) {
                    std::memset(&work[0], 0, (lastc + 1) * sizeof(double));
                }

                knt = 0;
                c_i = jA + 6 * lastc;
                for (b_i = jA; b_i <= c_i; b_i += 6) {
                    ix = iv0_tmp;
                    temp = 0.0;
                    d_i = (b_i + lastv) - 1;
                    for (k = b_i; k <= d_i; k++) {
                        temp += a[k - 1] * a[ix];
                        ix++;
                    }

                    work[knt] += temp;
                    knt++;
                }
            }

            if (!(-tau[i] == 0.0)) {
                knt = 0;
                for (b_i = 0; b_i <= lastc; b_i++) {
                    if (work[knt] != 0.0) {
                        temp = work[knt] * -tau[i];
                        ix = iv0_tmp;
                        c_i = lastv + jA;
                        for (coltop = jA; coltop < c_i; coltop++) {
                            a[coltop - 1] += a[ix] * temp;
                            ix++;
                        }
                    }

                    knt++;
                    jA += 6;
                }
            }
        }

        a[alpha1_tmp] = alpha1;
    }
}

//
// File trailer for xgehrd.cpp
//
// [EOF]
//
