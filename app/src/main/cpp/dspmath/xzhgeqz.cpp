//
// File: xzhgeqz.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 15-Oct-2020 21:35:42
//

// Include Files
#include "xzhgeqz.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "slmfunc.h"
#include "sqrt.h"
#include "xzlartg.h"
#include <cmath>
#include <cstring>
#include <string.h>

// Function Definitions

//
// Arguments    : const creal_T A[36]
//                int ilo
//                int ihi
//                int *info
//                creal_T alpha1[6]
//                creal_T beta1[6]
// Return Type  : void
//
void xzhgeqz(const creal_T A[36], int ilo, int ihi, int *info, creal_T alpha1[6],
             creal_T beta1[6]) {
    creal_T b_A[36];
    int i;
    double eshift_re;
    double eshift_im;
    creal_T ctemp;
    double anorm;
    double scale;
    double b_atol;
    boolean_T firstNonZero;
    int j;
    int ctemp_tmp;
    double ascale;
    int jp1;
    double temp1;
    boolean_T guard1 = false;
    boolean_T guard2 = false;
    double temp2;
    int ilast;
    creal_T shift;
    creal_T b_ascale;
    std::memcpy(&b_A[0], &A[0], 36U * sizeof(creal_T));
    *info = 0;
    for (i = 0; i < 6; i++) {
        alpha1[i].re = 0.0;
        alpha1[i].im = 0.0;
        beta1[i].re = 1.0;
        beta1[i].im = 0.0;
    }

    eshift_re = 0.0;
    eshift_im = 0.0;
    ctemp.re = 0.0;
    ctemp.im = 0.0;
    anorm = 0.0;
    if (ilo <= ihi) {
        scale = 0.0;
        anorm = 0.0;
        firstNonZero = true;
        for (j = ilo; j <= ihi; j++) {
            ctemp_tmp = j + 1;
            if (ihi < j + 1) {
                ctemp_tmp = ihi;
            }

            for (i = ilo; i <= ctemp_tmp; i++) {
                jp1 = (i + 6 * (j - 1)) - 1;
                if (A[jp1].re != 0.0) {
                    temp1 = std::abs(A[jp1].re);
                    if (firstNonZero) {
                        anorm = 1.0;
                        scale = temp1;
                        firstNonZero = false;
                    } else if (scale < temp1) {
                        temp2 = scale / temp1;
                        anorm = anorm * temp2 * temp2 + 1.0;
                        scale = temp1;
                    } else {
                        temp2 = temp1 / scale;
                        anorm += temp2 * temp2;
                    }
                }

                if (A[jp1].im != 0.0) {
                    temp1 = std::abs(A[jp1].im);
                    if (firstNonZero) {
                        anorm = 1.0;
                        scale = temp1;
                        firstNonZero = false;
                    } else if (scale < temp1) {
                        temp2 = scale / temp1;
                        anorm = anorm * temp2 * temp2 + 1.0;
                        scale = temp1;
                    } else {
                        temp2 = temp1 / scale;
                        anorm += temp2 * temp2;
                    }
                }
            }
        }

        anorm = scale * std::sqrt(anorm);
    }

    scale = 2.2204460492503131E-16 * anorm;
    b_atol = 2.2250738585072014E-308;
    if (scale > 2.2250738585072014E-308) {
        b_atol = scale;
    }

    scale = 2.2250738585072014E-308;
    if (anorm > 2.2250738585072014E-308) {
        scale = anorm;
    }

    ascale = 1.0 / scale;
    firstNonZero = true;
    ctemp_tmp = ihi + 1;
    for (j = ctemp_tmp; j < 7; j++) {
        alpha1[j - 1] = A[(j + 6 * (j - 1)) - 1];
    }

    guard1 = false;
    guard2 = false;
    if (ihi >= ilo) {
        int ifirst;
        int istart;
        int ilastm1;
        int ilastm;
        int iiter;
        boolean_T goto60;
        boolean_T goto70;
        boolean_T goto90;
        int jiter;
        ifirst = ilo;
        istart = ilo;
        ilast = ihi - 1;
        ilastm1 = ihi - 2;
        ilastm = ihi;
        iiter = 0;
        goto60 = false;
        goto70 = false;
        goto90 = false;
        jiter = 0;
        int exitg1;
        do {
            exitg1 = 0;
            if (jiter <= 30 * ((ihi - ilo) + 1) - 1) {
                boolean_T b_guard1 = false;
                boolean_T exitg2;
                b_guard1 = false;
                if (ilast + 1 == ilo) {
                    goto60 = true;
                    b_guard1 = true;
                } else {
                    ctemp_tmp = ilast + 6 * ilastm1;
                    if (std::abs(b_A[ctemp_tmp].re) + std::abs(b_A[ctemp_tmp].im) <=
                        b_atol) {
                        b_A[ctemp_tmp].re = 0.0;
                        b_A[ctemp_tmp].im = 0.0;
                        goto60 = true;
                        b_guard1 = true;
                    } else {
                        boolean_T guard3 = false;
                        j = ilastm1;
                        guard3 = false;
                        exitg2 = false;
                        while ((!exitg2) && (j + 1 >= ilo)) {
                            if (j + 1 == ilo) {
                                guard3 = true;
                                exitg2 = true;
                            } else {
                                ctemp_tmp = j + 6 * (j - 1);
                                if (std::abs(b_A[ctemp_tmp].re) + std::abs(b_A[ctemp_tmp].im) <=
                                    b_atol) {
                                    b_A[ctemp_tmp].re = 0.0;
                                    b_A[ctemp_tmp].im = 0.0;
                                    guard3 = true;
                                    exitg2 = true;
                                } else {
                                    j--;
                                    guard3 = false;
                                }
                            }
                        }

                        if (guard3) {
                            ifirst = j + 1;
                            goto70 = true;
                        }

                        if (goto70) {
                            b_guard1 = true;
                        } else {
                            for (i = 0; i < 6; i++) {
                                alpha1[i].re = rtNaN;
                                alpha1[i].im = 0.0;
                                beta1[i].re = rtNaN;
                                beta1[i].im = 0.0;
                            }

                            *info = 1;
                            exitg1 = 1;
                        }
                    }
                }

                if (b_guard1) {
                    if (goto60) {
                        goto60 = false;
                        alpha1[ilast] = b_A[ilast + 6 * ilast];
                        ilast = ilastm1;
                        ilastm1--;
                        if (ilast + 1 < ilo) {
                            firstNonZero = false;
                            guard2 = true;
                            exitg1 = 1;
                        } else {
                            iiter = 0;
                            eshift_re = 0.0;
                            eshift_im = 0.0;
                            ilastm = ilast + 1;
                            jiter++;
                        }
                    } else {
                        if (goto70) {
                            double ad22_re;
                            double ad22_im;
                            goto70 = false;
                            iiter++;
                            if (iiter - iiter / 10 * 10 != 0) {
                                double ascale_re;
                                double ascale_im;
                                double t1_re;
                                double t1_im;
                                double t1_im_tmp;
                                jp1 = ilastm1 + 6 * ilastm1;
                                anorm = ascale * b_A[jp1].re;
                                scale = ascale * b_A[jp1].im;
                                if (scale == 0.0) {
                                    shift.re = anorm / 0.40824829046386307;
                                    shift.im = 0.0;
                                } else if (anorm == 0.0) {
                                    shift.re = 0.0;
                                    shift.im = scale / 0.40824829046386307;
                                } else {
                                    shift.re = anorm / 0.40824829046386307;
                                    shift.im = scale / 0.40824829046386307;
                                }

                                jp1 = ilast + 6 * ilast;
                                anorm = ascale * b_A[jp1].re;
                                scale = ascale * b_A[jp1].im;
                                if (scale == 0.0) {
                                    ad22_re = anorm / 0.40824829046386307;
                                    ad22_im = 0.0;
                                } else if (anorm == 0.0) {
                                    ad22_re = 0.0;
                                    ad22_im = scale / 0.40824829046386307;
                                } else {
                                    ad22_re = anorm / 0.40824829046386307;
                                    ad22_im = scale / 0.40824829046386307;
                                }

                                t1_re = 0.5 * (shift.re + ad22_re);
                                t1_im = 0.5 * (shift.im + ad22_im);
                                t1_im_tmp = t1_re * t1_im;
                                jp1 = ilastm1 + 6 * ilast;
                                anorm = ascale * b_A[jp1].re;
                                scale = ascale * b_A[jp1].im;
                                if (scale == 0.0) {
                                    ascale_re = anorm / 0.40824829046386307;
                                    ascale_im = 0.0;
                                } else if (anorm == 0.0) {
                                    ascale_re = 0.0;
                                    ascale_im = scale / 0.40824829046386307;
                                } else {
                                    ascale_re = anorm / 0.40824829046386307;
                                    ascale_im = scale / 0.40824829046386307;
                                }

                                jp1 = ilast + 6 * ilastm1;
                                anorm = ascale * b_A[jp1].re;
                                scale = ascale * b_A[jp1].im;
                                if (scale == 0.0) {
                                    temp2 = anorm / 0.40824829046386307;
                                    anorm = 0.0;
                                } else if (anorm == 0.0) {
                                    temp2 = 0.0;
                                    anorm = scale / 0.40824829046386307;
                                } else {
                                    temp2 = anorm / 0.40824829046386307;
                                    anorm = scale / 0.40824829046386307;
                                }

                                scale = shift.re * ad22_re - shift.im * ad22_im;
                                temp1 = shift.re * ad22_im + shift.im * ad22_re;
                                shift.re = ((t1_re * t1_re - t1_im * t1_im) + (ascale_re * temp2
                                                                               -
                                                                               ascale_im * anorm)) -
                                           scale;
                                shift.im = ((t1_im_tmp + t1_im_tmp) + (ascale_re * anorm +
                                                                       ascale_im * temp2)) - temp1;
                                b_sqrt(&shift);
                                if ((t1_re - ad22_re) * shift.re + (t1_im - ad22_im) * shift.im <=
                                    0.0) {
                                    shift.re += t1_re;
                                    shift.im += t1_im;
                                } else {
                                    shift.re = t1_re - shift.re;
                                    shift.im = t1_im - shift.im;
                                }
                            } else {
                                double ascale_re;
                                double ascale_im;
                                jp1 = ilast + 6 * ilastm1;
                                anorm = ascale * b_A[jp1].re;
                                scale = ascale * b_A[jp1].im;
                                if (scale == 0.0) {
                                    ascale_re = anorm / 0.40824829046386307;
                                    ascale_im = 0.0;
                                } else if (anorm == 0.0) {
                                    ascale_re = 0.0;
                                    ascale_im = scale / 0.40824829046386307;
                                } else {
                                    ascale_re = anorm / 0.40824829046386307;
                                    ascale_im = scale / 0.40824829046386307;
                                }

                                eshift_re += ascale_re;
                                eshift_im += ascale_im;
                                shift.re = eshift_re;
                                shift.im = eshift_im;
                            }

                            j = ilastm1;
                            jp1 = ilastm1 + 1;
                            exitg2 = false;
                            while ((!exitg2) && (j + 1 > ifirst)) {
                                istart = j + 1;
                                ctemp_tmp = j + 6 * j;
                                ctemp.re = ascale * b_A[ctemp_tmp].re - shift.re *
                                                                        0.40824829046386307;
                                ctemp.im = ascale * b_A[ctemp_tmp].im - shift.im *
                                                                        0.40824829046386307;
                                anorm = std::abs(ctemp.re) + std::abs(ctemp.im);
                                jp1 += 6 * j;
                                temp2 = ascale * (std::abs(b_A[jp1].re) + std::abs(b_A[jp1].im));
                                scale = anorm;
                                if (temp2 > anorm) {
                                    scale = temp2;
                                }

                                if ((scale < 1.0) && (scale != 0.0)) {
                                    anorm /= scale;
                                    temp2 /= scale;
                                }

                                ctemp_tmp = j + 6 * (j - 1);
                                if ((std::abs(b_A[ctemp_tmp].re) + std::abs(b_A[ctemp_tmp].im)) *
                                    temp2 <= anorm * b_atol) {
                                    goto90 = true;
                                    exitg2 = true;
                                } else {
                                    jp1 = j;
                                    j--;
                                }
                            }

                            if (!goto90) {
                                istart = ifirst;
                                ctemp_tmp = (ifirst + 6 * (ifirst - 1)) - 1;
                                ctemp.re = ascale * b_A[ctemp_tmp].re - shift.re *
                                                                        0.40824829046386307;
                                ctemp.im = ascale * b_A[ctemp_tmp].im - shift.im *
                                                                        0.40824829046386307;
                            }

                            goto90 = false;
                            jp1 = istart + 6 * (istart - 1);
                            b_ascale.re = ascale * b_A[jp1].re;
                            b_ascale.im = ascale * b_A[jp1].im;
                            b_xzlartg(ctemp, b_ascale, &anorm, &shift);
                            j = istart;
                            jp1 = istart - 2;
                            while (j < ilast + 1) {
                                int ad22_re_tmp;
                                if (j > istart) {
                                    ctemp_tmp = j + 6 * jp1;
                                    xzlartg(b_A[ctemp_tmp - 1], b_A[ctemp_tmp], &anorm, &shift,
                                            &b_A[(j + 6 * jp1) - 1]);
                                    b_A[ctemp_tmp].re = 0.0;
                                    b_A[ctemp_tmp].im = 0.0;
                                }

                                for (jp1 = j; jp1 <= ilastm; jp1++) {
                                    ctemp_tmp = j + 6 * (jp1 - 1);
                                    ad22_re_tmp = ctemp_tmp - 1;
                                    ad22_re = anorm * b_A[ad22_re_tmp].re + (shift.re *
                                                                             b_A[ctemp_tmp].re -
                                                                             shift.im *
                                                                             b_A[ctemp_tmp].im);
                                    ad22_im = anorm * b_A[ad22_re_tmp].im + (shift.re *
                                                                             b_A[ctemp_tmp].im +
                                                                             shift.im *
                                                                             b_A[ctemp_tmp].re);
                                    scale = b_A[ad22_re_tmp].re;
                                    b_A[ctemp_tmp].re = anorm * b_A[ctemp_tmp].re - (shift.re *
                                                                                     b_A[ad22_re_tmp].re +
                                                                                     shift.im *
                                                                                     b_A[ad22_re_tmp].im);
                                    b_A[ctemp_tmp].im = anorm * b_A[ctemp_tmp].im - (shift.re *
                                                                                     b_A[ad22_re_tmp].im -
                                                                                     shift.im *
                                                                                     scale);
                                    b_A[ad22_re_tmp].re = ad22_re;
                                    b_A[ad22_re_tmp].im = ad22_im;
                                }

                                shift.re = -shift.re;
                                shift.im = -shift.im;
                                jp1 = j;
                                if (ilast + 1 < j + 2) {
                                    jp1 = ilast - 1;
                                }

                                for (i = ifirst; i <= jp1 + 2; i++) {
                                    ctemp_tmp = (i + 6 * (j - 1)) - 1;
                                    ad22_re_tmp = (i + 6 * j) - 1;
                                    ad22_re = anorm * b_A[ad22_re_tmp].re + (shift.re *
                                                                             b_A[ctemp_tmp].re -
                                                                             shift.im *
                                                                             b_A[ctemp_tmp].im);
                                    ad22_im = anorm * b_A[ad22_re_tmp].im + (shift.re *
                                                                             b_A[ctemp_tmp].im +
                                                                             shift.im *
                                                                             b_A[ctemp_tmp].re);
                                    scale = b_A[ad22_re_tmp].re;
                                    b_A[ctemp_tmp].re = anorm * b_A[ctemp_tmp].re - (shift.re *
                                                                                     b_A[ad22_re_tmp].re +
                                                                                     shift.im *
                                                                                     b_A[ad22_re_tmp].im);
                                    b_A[ctemp_tmp].im = anorm * b_A[ctemp_tmp].im - (shift.re *
                                                                                     b_A[ad22_re_tmp].im -
                                                                                     shift.im *
                                                                                     scale);
                                    b_A[ad22_re_tmp].re = ad22_re;
                                    b_A[ad22_re_tmp].im = ad22_im;
                                }

                                jp1 = j - 1;
                                j++;
                            }
                        }

                        jiter++;
                    }
                }
            } else {
                guard2 = true;
                exitg1 = 1;
            }
        } while (exitg1 == 0);
    } else {
        guard1 = true;
    }

    if (guard2) {
        if (firstNonZero) {
            *info = ilast + 1;
            for (jp1 = 0; jp1 <= ilast; jp1++) {
                alpha1[jp1].re = rtNaN;
                alpha1[jp1].im = 0.0;
                beta1[jp1].re = rtNaN;
                beta1[jp1].im = 0.0;
            }
        } else {
            guard1 = true;
        }
    }

    if (guard1) {
        for (j = 0; j <= ilo - 2; j++) {
            alpha1[j] = b_A[j + 6 * j];
        }
    }
}

//
// File trailer for xzhgeqz.cpp
//
// [EOF]
//
