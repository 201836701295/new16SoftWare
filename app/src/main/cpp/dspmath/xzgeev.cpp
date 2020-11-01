//
// File: xzgeev.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "xzgeev.h"
#include "aweight.h"
#include "cweight.h"
#include "dspmath_rtwutil.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "xdlanv2.h"
#include "xzhgeqz.h"
#include "xzlartg.h"
#include "zweight.h"
#include <string.h>

// Function Definitions

//
// Arguments    : const double A[36]
//                int *info
//                creal_T alpha1[6]
//                creal_T beta1[6]
// Return Type  : void
//
void xzgeev(const double A[36], int *info, creal_T alpha1[6], creal_T beta1[6]) {
    int ii;
    creal_T At[36];
    double anrm;
    int jcol;
    boolean_T exitg1;
    double absxk;
    creal_T atmp;
    for (ii = 0; ii < 36; ii++) {
        At[ii].re = A[ii];
        At[ii].im = 0.0;
    }

    *info = 0;
    anrm = 0.0;
    jcol = 0;
    exitg1 = false;
    while ((!exitg1) && (jcol < 36)) {
        absxk = rt_hypotd_snf(At[jcol].re, At[jcol].im);
        if (rtIsNaN(absxk)) {
            anrm = rtNaN;
            exitg1 = true;
        } else {
            if (absxk > anrm) {
                anrm = absxk;
            }

            jcol++;
        }
    }

    if (rtIsInf(anrm) || rtIsNaN(anrm)) {
        for (int i = 0; i < 6; i++) {
            alpha1[i].re = rtNaN;
            alpha1[i].im = 0.0;
            beta1[i].re = rtNaN;
            beta1[i].im = 0.0;
        }
    } else {
        boolean_T ilascl;
        int i;
        double anrmto;
        boolean_T guard1 = false;
        int ilo;
        double ctoc;
        int ihi;
        boolean_T notdone;
        int exitg3;
        double stemp_im;
        int j;
        double cto1;
        double a;
        int nzcount;
        boolean_T exitg4;
        int At_tmp;
        ilascl = false;
        anrmto = anrm;
        guard1 = false;
        if ((anrm > 0.0) && (anrm < 6.7178761075670888E-139)) {
            anrmto = 6.7178761075670888E-139;
            ilascl = true;
            guard1 = true;
        } else {
            if (anrm > 1.4885657073574029E+138) {
                anrmto = 1.4885657073574029E+138;
                ilascl = true;
                guard1 = true;
            }
        }

        if (guard1) {
            absxk = anrm;
            ctoc = anrmto;
            notdone = true;
            while (notdone) {
                stemp_im = absxk * 2.0041683600089728E-292;
                cto1 = ctoc / 4.9896007738368E+291;
                if ((stemp_im > ctoc) && (ctoc != 0.0)) {
                    a = 2.0041683600089728E-292;
                    absxk = stemp_im;
                } else if (cto1 > absxk) {
                    a = 4.9896007738368E+291;
                    ctoc = cto1;
                } else {
                    a = ctoc / absxk;
                    notdone = false;
                }

                for (ii = 0; ii < 36; ii++) {
                    At[ii].re *= a;
                    At[ii].im *= a;
                }
            }
        }

        ilo = 1;
        ihi = 6;
        do {
            exitg3 = 0;
            i = 0;
            j = 0;
            notdone = false;
            ii = ihi;
            exitg1 = false;
            while ((!exitg1) && (ii > 0)) {
                nzcount = 0;
                i = ii;
                j = ihi;
                jcol = 0;
                exitg4 = false;
                while ((!exitg4) && (jcol <= ihi - 1)) {
                    At_tmp = (ii + 6 * jcol) - 1;
                    if ((At[At_tmp].re != 0.0) || (At[At_tmp].im != 0.0) || (ii == jcol +
                                                                                   1)) {
                        if (nzcount == 0) {
                            j = jcol + 1;
                            nzcount = 1;
                            jcol++;
                        } else {
                            nzcount = 2;
                            exitg4 = true;
                        }
                    } else {
                        jcol++;
                    }
                }

                if (nzcount < 2) {
                    notdone = true;
                    exitg1 = true;
                } else {
                    ii--;
                }
            }

            if (!notdone) {
                exitg3 = 2;
            } else {
                if (i != ihi) {
                    for (jcol = 0; jcol < 6; jcol++) {
                        ii = (i + 6 * jcol) - 1;
                        atmp = At[ii];
                        At_tmp = (ihi + 6 * jcol) - 1;
                        At[ii] = At[At_tmp];
                        At[At_tmp] = atmp;
                    }
                }

                if (j != ihi) {
                    for (jcol = 0; jcol < ihi; jcol++) {
                        ii = jcol + 6 * (j - 1);
                        atmp = At[ii];
                        At_tmp = jcol + 6 * (ihi - 1);
                        At[ii] = At[At_tmp];
                        At[At_tmp] = atmp;
                    }
                }

                ihi--;
                if (ihi == 1) {
                    exitg3 = 1;
                }
            }
        } while (exitg3 == 0);

        if (exitg3 != 1) {
            int exitg2;
            do {
                exitg2 = 0;
                i = 0;
                j = 0;
                notdone = false;
                jcol = ilo;
                exitg1 = false;
                while ((!exitg1) && (jcol <= ihi)) {
                    nzcount = 0;
                    i = ihi;
                    j = jcol;
                    ii = ilo;
                    exitg4 = false;
                    while ((!exitg4) && (ii <= ihi)) {
                        At_tmp = (ii + 6 * (jcol - 1)) - 1;
                        if ((At[At_tmp].re != 0.0) || (At[At_tmp].im != 0.0) || (ii == jcol)) {
                            if (nzcount == 0) {
                                i = ii;
                                nzcount = 1;
                                ii++;
                            } else {
                                nzcount = 2;
                                exitg4 = true;
                            }
                        } else {
                            ii++;
                        }
                    }

                    if (nzcount < 2) {
                        notdone = true;
                        exitg1 = true;
                    } else {
                        jcol++;
                    }
                }

                if (!notdone) {
                    exitg2 = 1;
                } else {
                    if (i != ilo) {
                        for (jcol = ilo; jcol < 7; jcol++) {
                            ii = 6 * (jcol - 1);
                            nzcount = (i + ii) - 1;
                            atmp = At[nzcount];
                            At_tmp = (ilo + ii) - 1;
                            At[nzcount] = At[At_tmp];
                            At[At_tmp] = atmp;
                        }
                    }

                    if (j != ilo) {
                        for (jcol = 0; jcol < ihi; jcol++) {
                            ii = jcol + 6 * (j - 1);
                            atmp = At[ii];
                            At_tmp = jcol + 6 * (ilo - 1);
                            At[ii] = At[At_tmp];
                            At[At_tmp] = atmp;
                        }
                    }

                    ilo++;
                    if (ilo == ihi) {
                        exitg2 = 1;
                    }
                }
            } while (exitg2 == 0);
        }

        if (ihi >= ilo + 2) {
            for (jcol = ilo - 1; jcol + 1 < ihi - 1; jcol++) {
                int jcolp1;
                jcolp1 = jcol + 2;
                for (int jrow = ihi - 1; jrow + 1 > jcol + 2; jrow--) {
                    At_tmp = jrow + 6 * jcol;
                    xzlartg(At[At_tmp - 1], At[At_tmp], &absxk, &atmp, &At[(jrow + 6 *
                                                                                   jcol) - 1]);
                    At[At_tmp].re = 0.0;
                    At[At_tmp].im = 0.0;
                    for (j = jcolp1; j < 7; j++) {
                        ii = jrow + 6 * (j - 1);
                        nzcount = ii - 1;
                        ctoc = absxk * At[nzcount].re + (atmp.re * At[ii].re - atmp.im *
                                                                               At[ii].im);
                        stemp_im = absxk * At[nzcount].im + (atmp.re * At[ii].im + atmp.im *
                                                                                   At[ii].re);
                        cto1 = At[nzcount].re;
                        At[ii].re = absxk * At[ii].re - (atmp.re * At[nzcount].re + atmp.im *
                                                                                    At[nzcount].im);
                        At[ii].im = absxk * At[ii].im - (atmp.re * At[nzcount].im - atmp.im *
                                                                                    cto1);
                        At[nzcount].re = ctoc;
                        At[nzcount].im = stemp_im;
                    }

                    atmp.re = -atmp.re;
                    atmp.im = -atmp.im;
                    for (i = 1; i <= ihi; i++) {
                        ii = (i + 6 * (jrow - 1)) - 1;
                        nzcount = (i + 6 * jrow) - 1;
                        ctoc = absxk * At[nzcount].re + (atmp.re * At[ii].re - atmp.im *
                                                                               At[ii].im);
                        stemp_im = absxk * At[nzcount].im + (atmp.re * At[ii].im + atmp.im *
                                                                                   At[ii].re);
                        cto1 = At[nzcount].re;
                        At[ii].re = absxk * At[ii].re - (atmp.re * At[nzcount].re + atmp.im *
                                                                                    At[nzcount].im);
                        At[ii].im = absxk * At[ii].im - (atmp.re * At[nzcount].im - atmp.im *
                                                                                    cto1);
                        At[nzcount].re = ctoc;
                        At[nzcount].im = stemp_im;
                    }
                }
            }
        }

        xzhgeqz(At, ilo, ihi, info, alpha1, beta1);
        if ((*info == 0) && ilascl) {
            notdone = true;
            while (notdone) {
                stemp_im = anrmto * 2.0041683600089728E-292;
                cto1 = anrm / 4.9896007738368E+291;
                if ((stemp_im > anrm) && (anrm != 0.0)) {
                    a = 2.0041683600089728E-292;
                    anrmto = stemp_im;
                } else if (cto1 > anrmto) {
                    a = 4.9896007738368E+291;
                    anrm = cto1;
                } else {
                    a = anrm / anrmto;
                    notdone = false;
                }

                for (ii = 0; ii < 6; ii++) {
                    alpha1[ii].re *= a;
                    alpha1[ii].im *= a;
                }
            }
        }
    }
}

//
// File trailer for xzgeev.cpp
//
// [EOF]
//
