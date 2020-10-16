//
// File: butter.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 15-Oct-2020 21:35:42
//

// Include Files
#include "butter.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_defines.h"
#include "rt_nonfinite.h"
#include "slmfunc.h"
#include "xdhseqr.h"
#include "xgehrd.h"
#include "xgetrf.h"
#include "xzgeev.h"
#include <cmath>
#include <cstring>
#include <math.h>
#include <string.h>

// Function Declarations
static double rt_atan2d_snf(double u0, double u1);

// Function Definitions

//
// Arguments    : double u0
//                double u1
// Return Type  : double
//
static double rt_atan2d_snf(double u0, double u1) {
    double y;
    if (rtIsNaN(u0) || rtIsNaN(u1)) {
        y = rtNaN;
    } else if (rtIsInf(u0) && rtIsInf(u1)) {
        int b_u0;
        int b_u1;
        if (u0 > 0.0) {
            b_u0 = 1;
        } else {
            b_u0 = -1;
        }

        if (u1 > 0.0) {
            b_u1 = 1;
        } else {
            b_u1 = -1;
        }

        y = atan2(static_cast<double>(b_u0), static_cast<double>(b_u1));
    } else if (u1 == 0.0) {
        if (u0 > 0.0) {
            y = RT_PI / 2.0;
        } else if (u0 < 0.0) {
            y = -(RT_PI / 2.0);
        } else {
            y = 0.0;
        }
    } else {
        y = atan2(u0, u1);
    }

    return y;
}

//
// Arguments    : const double Wn[2]
//                double varargout_1[7]
//                double varargout_2[7]
// Return Type  : void
//
void butter(const double Wn[2], double varargout_1[7], double varargout_2[7]) {
    double u_idx_0;
    double u_idx_1;
    double a[9];
    double r;
    int istart;
    double q;
    double b_a[4];
    signed char b1[2];
    int i;
    double Wn1;
    double t1_tmp[36];
    double b_t1_tmp[36];
    static const signed char iv[18] = {-1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, 0,
                                       0, 0, 0, 0, 0};

    static const signed char iv1[9] = {1, 0, 0, 0, 1, 0, 0, 0, 1};

    int jBcol;
    int ipiv[6];
    double T[36];
    int b_i;
    int j;
    boolean_T p;
    int kAcol;
    int T_tmp;
    creal_T alpha1[6];
    creal_T kern[7];
    creal_T beta1[6];
    double kern_im;
    double re;
    double kern_re;
    double im;
    double b_kern_im;
    static const signed char c_a[7] = {1, 0, -3, 0, 3, 0, -1};

    static const signed char iv2[7] = {1, 0, -3, 0, 3, 0, -1};

    u_idx_0 = 4.0 * std::tan(3.1415926535897931 * Wn[0] / 2.0);
    u_idx_1 = 4.0 * std::tan(3.1415926535897931 * Wn[1] / 2.0);
    std::memset(&a[0], 0, 9U * sizeof(double));
    a[0] = -1.0;
    r = 0.49999999999999978;
    for (istart = 2; istart >= 2; istart--) {
        r -= -0.49999999999999978;
    }

    q = (1.0 - -r * 0.0) / 1.0000000000000002;
    b_a[1] = q;
    b_a[0] = -r - q * 0.0;
    b_a[3] = 0.0;
    b_a[2] = -0.99999999999999989;
    b1[1] = 0;
    b1[0] = 1;
    for (i = 0; i < 2; i++) {
        a[i + 1] = b1[i];
        r = b_a[i + 2];
        a[i + 4] = b_a[i] + r * 0.0;
        a[i + 7] = b_a[i] * 0.0 + r * 1.0000000000000002;
    }

    Wn1 = std::sqrt(u_idx_0 * u_idx_1);
    q = Wn1 / (u_idx_1 - u_idx_0);
    std::memset(&t1_tmp[0], 0, 36U * sizeof(double));
    for (istart = 0; istart < 6; istart++) {
        t1_tmp[istart + 6 * istart] = 1.0;
    }

    for (i = 0; i < 3; i++) {
        b_t1_tmp[6 * i] = Wn1 * (a[3 * i] / q) * 0.5 / 2.0;
        istart = 6 * (i + 3);
        b_t1_tmp[istart] = Wn1 * static_cast<double>(iv1[3 * i]) * 0.5 / 2.0;
        jBcol = 3 * i + 1;
        b_t1_tmp[6 * i + 1] = Wn1 * (a[jBcol] / q) * 0.5 / 2.0;
        b_t1_tmp[istart + 1] = Wn1 * static_cast<double>(iv1[jBcol]) * 0.5 / 2.0;
        jBcol = 3 * i + 2;
        b_t1_tmp[6 * i + 2] = Wn1 * (a[jBcol] / q) * 0.5 / 2.0;
        b_t1_tmp[istart + 2] = Wn1 * static_cast<double>(iv1[jBcol]) * 0.5 / 2.0;
    }

    for (i = 0; i < 6; i++) {
        b_t1_tmp[6 * i + 3] = Wn1 * static_cast<double>(iv[3 * i]) * 0.5 / 2.0;
        b_t1_tmp[6 * i + 4] = Wn1 * static_cast<double>(iv[3 * i + 1]) * 0.5 / 2.0;
        b_t1_tmp[6 * i + 5] = Wn1 * static_cast<double>(iv[3 * i + 2]) * 0.5 / 2.0;
    }

    for (i = 0; i < 36; i++) {
        T[i] = t1_tmp[i] + b_t1_tmp[i];
        t1_tmp[i] -= b_t1_tmp[i];
    }

    xgetrf(t1_tmp, ipiv, &istart);
    for (b_i = 0; b_i < 5; b_i++) {
        if (ipiv[b_i] != b_i + 1) {
            for (j = 0; j < 6; j++) {
                istart = b_i + 6 * j;
                q = T[istart];
                T_tmp = (ipiv[b_i] + 6 * j) - 1;
                T[istart] = T[T_tmp];
                T[T_tmp] = q;
            }
        }
    }

    for (j = 0; j < 6; j++) {
        jBcol = 6 * j;
        for (istart = 0; istart < 6; istart++) {
            kAcol = 6 * istart;
            i = istart + jBcol;
            if (T[i] != 0.0) {
                int i1;
                i1 = istart + 2;
                for (b_i = i1; b_i < 7; b_i++) {
                    T_tmp = (b_i + jBcol) - 1;
                    T[T_tmp] -= T[i] * t1_tmp[(b_i + kAcol) - 1];
                }
            }
        }
    }

    for (j = 0; j < 6; j++) {
        jBcol = 6 * j;
        for (istart = 5; istart >= 0; istart--) {
            kAcol = 6 * istart;
            i = istart + jBcol;
            if (T[i] != 0.0) {
                T[i] /= t1_tmp[istart + kAcol];
                for (b_i = 0; b_i < istart; b_i++) {
                    T_tmp = b_i + jBcol;
                    T[T_tmp] -= T[i] * t1_tmp[b_i + kAcol];
                }
            }
        }
    }

    p = true;
    for (istart = 0; istart < 36; istart++) {
        if ((!p) || (rtIsInf(T[istart]) || rtIsNaN(T[istart]))) {
            p = false;
        }
    }

    if (!p) {
        for (b_i = 0; b_i < 6; b_i++) {
            alpha1[b_i].re = rtNaN;
            alpha1[b_i].im = 0.0;
        }
    } else {
        boolean_T exitg2;
        p = true;
        j = 0;
        exitg2 = false;
        while ((!exitg2) && (j < 6)) {
            int exitg1;
            b_i = 0;
            do {
                exitg1 = 0;
                if (b_i <= j) {
                    if (!(T[b_i + 6 * j] == T[j + 6 * b_i])) {
                        p = false;
                        exitg1 = 1;
                    } else {
                        b_i++;
                    }
                } else {
                    j++;
                    exitg1 = 2;
                }
            } while (exitg1 == 0);

            if (exitg1 == 1) {
                exitg2 = true;
            }
        }

        if (p) {
            p = true;
            for (istart = 0; istart < 36; istart++) {
                if ((!p) || (rtIsInf(T[istart]) || rtIsNaN(T[istart]))) {
                    p = false;
                }
            }

            if (!p) {
                for (i = 0; i < 36; i++) {
                    T[i] = rtNaN;
                }

                istart = 2;
                for (j = 0; j < 5; j++) {
                    if (istart <= 6) {
                        std::memset(&T[(j * 6 + istart) + -1], 0, (7 - istart) * sizeof
                                (double));
                    }

                    istart++;
                }
            } else {
                xgehrd(T);
                eml_dlahqr(T);
                istart = 4;
                for (j = 0; j < 3; j++) {
                    if (istart <= 6) {
                        std::memset(&T[(j * 6 + istart) + -1], 0, (7 - istart) * sizeof
                                (double));
                    }

                    istart++;
                }
            }

            for (istart = 0; istart < 6; istart++) {
                alpha1[istart].re = T[istart + 6 * istart];
                alpha1[istart].im = 0.0;
            }
        } else {
            xzgeev(T, &istart, alpha1, beta1);
            for (i = 0; i < 6; i++) {
                if (beta1[i].im == 0.0) {
                    if (alpha1[i].im == 0.0) {
                        re = alpha1[i].re / beta1[i].re;
                        im = 0.0;
                    } else if (alpha1[i].re == 0.0) {
                        re = 0.0;
                        im = alpha1[i].im / beta1[i].re;
                    } else {
                        re = alpha1[i].re / beta1[i].re;
                        im = alpha1[i].im / beta1[i].re;
                    }
                } else if (beta1[i].re == 0.0) {
                    if (alpha1[i].re == 0.0) {
                        re = alpha1[i].im / beta1[i].im;
                        im = 0.0;
                    } else if (alpha1[i].im == 0.0) {
                        re = 0.0;
                        im = -(alpha1[i].re / beta1[i].im);
                    } else {
                        re = alpha1[i].im / beta1[i].im;
                        im = -(alpha1[i].re / beta1[i].im);
                    }
                } else {
                    r = std::abs(beta1[i].re);
                    q = std::abs(beta1[i].im);
                    if (r > q) {
                        r = beta1[i].im / beta1[i].re;
                        q = beta1[i].re + r * beta1[i].im;
                        re = (alpha1[i].re + r * alpha1[i].im) / q;
                        im = (alpha1[i].im - r * alpha1[i].re) / q;
                    } else if (q == r) {
                        if (beta1[i].re > 0.0) {
                            q = 0.5;
                        } else {
                            q = -0.5;
                        }

                        if (beta1[i].im > 0.0) {
                            u_idx_0 = 0.5;
                        } else {
                            u_idx_0 = -0.5;
                        }

                        re = (alpha1[i].re * q + alpha1[i].im * u_idx_0) / r;
                        im = (alpha1[i].im * q - alpha1[i].re * u_idx_0) / r;
                    } else {
                        r = beta1[i].re / beta1[i].im;
                        q = beta1[i].im + r * beta1[i].re;
                        re = (r * alpha1[i].re + alpha1[i].im) / q;
                        im = (r * alpha1[i].im - alpha1[i].re) / q;
                    }
                }

                alpha1[i].re = re;
                alpha1[i].im = im;
            }
        }
    }

    kern[0].re = 1.0;
    kern[0].im = 0.0;
    for (j = 0; j < 6; j++) {
        r = kern[j].re;
        kern[j + 1].re = -alpha1[j].re * kern[j].re - -alpha1[j].im * kern[j].im;
        kern[j + 1].im = -alpha1[j].re * kern[j].im + -alpha1[j].im * r;
        for (istart = j + 1; istart >= 2; istart--) {
            q = alpha1[j].re * kern[istart - 2].im + alpha1[j].im * kern[istart - 2].
                    re;
            kern[istart - 1].re -= alpha1[j].re * kern[istart - 2].re - alpha1[j].im *
                                                                        kern[istart - 2].im;
            kern[istart - 1].im -= q;
        }
    }

    u_idx_0 = 2.0 * rt_atan2d_snf(Wn1, 4.0);
    q = u_idx_0 * 0.0;
    Wn1 = 0.0;
    kern_im = 0.0;
    kern_re = 0.0;
    b_kern_im = 0.0;
    for (istart = 0; istart < 7; istart++) {
        u_idx_1 = kern[istart].re;
        varargout_2[istart] = kern[istart].re;
        re = static_cast<double>(istart) * q;
        im = static_cast<double>(istart) * -u_idx_0;
        kern[istart].re = re;
        kern[istart].im = im;
        if (im == 0.0) {
            re = std::exp(re);
            im = 0.0;
            kern[istart].re = re;
            kern[istart].im = 0.0;
        } else {
            r = std::exp(re / 2.0);
            re = r * (r * std::cos(im));
            im = r * (r * std::sin(im));
            kern[istart].re = re;
            kern[istart].im = im;
        }

        Wn1 += re * u_idx_1 - im * 0.0;
        kern_im += re * 0.0 + im * u_idx_1;
        kern_re += re * static_cast<double>(iv2[istart]) - im * 0.0;
        b_kern_im += re * 0.0 + im * static_cast<double>(iv2[istart]);
    }

    for (i = 0; i < 7; i++) {
        u_idx_0 = static_cast<double>(c_a[i]) * Wn1;
        u_idx_1 = static_cast<double>(c_a[i]) * kern_im;
        if (b_kern_im == 0.0) {
            if (u_idx_1 == 0.0) {
                q = u_idx_0 / kern_re;
            } else if (u_idx_0 == 0.0) {
                q = 0.0;
            } else {
                q = u_idx_0 / kern_re;
            }
        } else if (kern_re == 0.0) {
            if (u_idx_0 == 0.0) {
                q = u_idx_1 / b_kern_im;
            } else if (u_idx_1 == 0.0) {
                q = 0.0;
            } else {
                q = u_idx_1 / b_kern_im;
            }
        } else {
            r = std::abs(kern_re);
            q = std::abs(b_kern_im);
            if (r > q) {
                r = b_kern_im / kern_re;
                q = (u_idx_0 + r * u_idx_1) / (kern_re + r * b_kern_im);
            } else if (q == r) {
                if (kern_re > 0.0) {
                    q = 0.5;
                } else {
                    q = -0.5;
                }

                if (b_kern_im > 0.0) {
                    re = 0.5;
                } else {
                    re = -0.5;
                }

                q = (u_idx_0 * q + u_idx_1 * re) / r;
            } else {
                r = kern_re / b_kern_im;
                q = (r * u_idx_0 + u_idx_1) / (b_kern_im + r * kern_re);
            }
        }

        varargout_1[i] = q;
    }
}

//
// File trailer for butter.cpp
//
// [EOF]
//
