//
// File: xdhseqr.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 15-Oct-2020 21:35:42
//

// Include Files
#include "xdhseqr.h"
#include "dspmath_rtwutil.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "slmfunc.h"
#include "xdlanv2.h"
#include "xnrm2.h"
#include <cmath>
#include <string.h>

// Function Definitions

//
// Arguments    : double h[36]
// Return Type  : int
//
int eml_dlahqr(double h[36]) {
    int info;
    double v[3];
    int i;
    boolean_T exitg1;
    double s;
    double ba;
    int knt;
    double d;
    double tst;
    double bb;
    double aa;
    double ab;
    double h22;
    double rt1r;
    int iy;
    info = 0;
    v[0] = 0.0;
    h[2] = 0.0;
    h[3] = 0.0;
    v[1] = 0.0;
    h[9] = 0.0;
    h[10] = 0.0;
    v[2] = 0.0;
    h[16] = 0.0;
    h[17] = 0.0;
    h[23] = 0.0;
    i = 5;
    exitg1 = false;
    while ((!exitg1) && (i + 1 >= 1)) {
        int L;
        boolean_T goto150;
        int its;
        boolean_T exitg2;
        int k;
        int b_i;
        int hoffset;
        int nr;
        L = 1;
        goto150 = false;
        its = 0;
        exitg2 = false;
        while ((!exitg2) && (its < 301)) {
            boolean_T exitg3;
            k = i;
            exitg3 = false;
            while ((!exitg3) && (k + 1 > L)) {
                b_i = k + 6 * (k - 1);
                ba = std::abs(h[b_i]);
                if (ba <= 6.0125050800269183E-292) {
                    exitg3 = true;
                } else {
                    knt = k + 6 * k;
                    bb = std::abs(h[knt]);
                    hoffset = b_i - 1;
                    tst = std::abs(h[hoffset]) + bb;
                    if (tst == 0.0) {
                        if (k - 1 >= 1) {
                            tst = std::abs(h[(k + 6 * (k - 2)) - 1]);
                        }

                        if (k + 2 <= 6) {
                            tst += std::abs(h[knt + 1]);
                        }
                    }

                    if (ba <= 2.2204460492503131E-16 * tst) {
                        tst = std::abs(h[knt - 1]);
                        if (ba > tst) {
                            ab = ba;
                            ba = tst;
                        } else {
                            ab = tst;
                        }

                        tst = std::abs(h[hoffset] - h[knt]);
                        if (bb > tst) {
                            aa = bb;
                            bb = tst;
                        } else {
                            aa = tst;
                        }

                        s = aa + ab;
                        tst = 2.2204460492503131E-16 * (bb * (aa / s));
                        if ((6.0125050800269183E-292 > tst) || rtIsNaN(tst)) {
                            tst = 6.0125050800269183E-292;
                        }

                        if (ba * (ab / s) <= tst) {
                            exitg3 = true;
                        } else {
                            k--;
                        }
                    } else {
                        k--;
                    }
                }
            }

            L = k + 1;
            if (k + 1 > 1) {
                h[k + 6 * (k - 1)] = 0.0;
            }

            if (k + 1 >= i) {
                goto150 = true;
                exitg2 = true;
            } else {
                int m;
                if (its == 10) {
                    hoffset = k + 6 * k;
                    s = std::abs(h[hoffset + 1]) + std::abs(h[(k + 6 * (k + 1)) + 2]);
                    tst = 0.75 * s + h[hoffset];
                    aa = -0.4375 * s;
                    ab = s;
                    h22 = tst;
                } else if (its == 20) {
                    s = std::abs(h[i + 6 * (i - 1)]) + std::abs(h[(i + 6 * (i - 2)) - 1]);
                    tst = 0.75 * s + h[i + 6 * i];
                    aa = -0.4375 * s;
                    ab = s;
                    h22 = tst;
                } else {
                    hoffset = i + 6 * (i - 1);
                    tst = h[hoffset - 1];
                    ab = h[hoffset];
                    aa = h[(i + 6 * i) - 1];
                    h22 = h[i + 6 * i];
                }

                s = ((std::abs(tst) + std::abs(aa)) + std::abs(ab)) + std::abs(h22);
                if (s == 0.0) {
                    rt1r = 0.0;
                    tst = 0.0;
                    ba = 0.0;
                    aa = 0.0;
                } else {
                    tst /= s;
                    ab /= s;
                    aa /= s;
                    h22 /= s;
                    bb = (tst + h22) / 2.0;
                    tst = (tst - bb) * (h22 - bb) - aa * ab;
                    aa = std::sqrt(std::abs(tst));
                    if (tst >= 0.0) {
                        rt1r = bb * s;
                        ba = rt1r;
                        tst = aa * s;
                        aa = -tst;
                    } else {
                        rt1r = bb + aa;
                        ba = bb - aa;
                        if (std::abs(rt1r - h22) <= std::abs(ba - h22)) {
                            rt1r *= s;
                            ba = rt1r;
                        } else {
                            ba *= s;
                            rt1r = ba;
                        }

                        tst = 0.0;
                        aa = 0.0;
                    }
                }

                m = i - 1;
                exitg3 = false;
                while ((!exitg3) && (m >= k + 1)) {
                    hoffset = m + 6 * (m - 1);
                    knt = hoffset - 1;
                    ab = h[knt] - ba;
                    s = (std::abs(ab) + std::abs(aa)) + std::abs(h[hoffset]);
                    bb = h[hoffset] / s;
                    hoffset = m + 6 * m;
                    v[0] = (bb * h[hoffset - 1] + (h[knt] - rt1r) * (ab / s)) - tst * (aa /
                                                                                       s);
                    v[1] = bb * (((h[knt] + h[hoffset]) - rt1r) - ba);
                    v[2] = bb * h[hoffset + 1];
                    s = (std::abs(v[0]) + std::abs(v[1])) + std::abs(v[2]);
                    v[0] /= s;
                    v[1] /= s;
                    v[2] /= s;
                    if (m == k + 1) {
                        exitg3 = true;
                    } else {
                        b_i = m + 6 * (m - 2);
                        if (std::abs(h[b_i - 1]) * (std::abs(v[1]) + std::abs(v[2])) <=
                            2.2204460492503131E-16 * std::abs(v[0]) * ((std::abs(h[b_i - 2])
                                                                        + std::abs(h[knt])) +
                                                                       std::abs(h[hoffset]))) {
                            exitg3 = true;
                        } else {
                            m--;
                        }
                    }
                }

                for (int b_k = m; b_k <= i; b_k++) {
                    int j;
                    nr = (i - b_k) + 2;
                    if (3 < nr) {
                        nr = 3;
                    }

                    if (b_k > m) {
                        hoffset = (b_k + 6 * (b_k - 2)) - 1;
                        for (j = 0; j < nr; j++) {
                            v[j] = h[j + hoffset];
                        }
                    }

                    aa = v[0];
                    bb = 0.0;
                    if (nr > 0) {
                        tst = b_xnrm2(nr - 1, v);
                        if (tst != 0.0) {
                            ab = rt_hypotd_snf(v[0], tst);
                            if (v[0] >= 0.0) {
                                ab = -ab;
                            }

                            if (std::abs(ab) < 1.0020841800044864E-292) {
                                knt = -1;
                                do {
                                    knt++;
                                    for (iy = 2; iy <= nr; iy++) {
                                        v[iy - 1] *= 9.9792015476736E+291;
                                    }

                                    ab *= 9.9792015476736E+291;
                                    aa *= 9.9792015476736E+291;
                                } while (!(std::abs(ab) >= 1.0020841800044864E-292));

                                ab = rt_hypotd_snf(aa, b_xnrm2(nr - 1, v));
                                if (aa >= 0.0) {
                                    ab = -ab;
                                }

                                bb = (ab - aa) / ab;
                                tst = 1.0 / (aa - ab);
                                for (iy = 2; iy <= nr; iy++) {
                                    v[iy - 1] *= tst;
                                }

                                for (iy = 0; iy <= knt; iy++) {
                                    ab *= 1.0020841800044864E-292;
                                }

                                aa = ab;
                            } else {
                                bb = (ab - v[0]) / ab;
                                tst = 1.0 / (v[0] - ab);
                                for (iy = 2; iy <= nr; iy++) {
                                    v[iy - 1] *= tst;
                                }

                                aa = ab;
                            }
                        }
                    }

                    v[0] = aa;
                    if (b_k > m) {
                        h[(b_k + 6 * (b_k - 2)) - 1] = aa;
                        b_i = b_k + 6 * (b_k - 2);
                        h[b_i] = 0.0;
                        if (b_k < i) {
                            h[b_i + 1] = 0.0;
                        }
                    } else {
                        if (m > k + 1) {
                            h[(b_k + 6 * (b_k - 2)) - 1] *= 1.0 - bb;
                        }
                    }

                    s = v[1];
                    tst = bb * v[1];
                    if (nr == 3) {
                        d = v[2];
                        ab = bb * v[2];
                        for (j = b_k; j < 7; j++) {
                            iy = b_k + 6 * (j - 1);
                            hoffset = iy - 1;
                            knt = iy + 1;
                            aa = (h[hoffset] + s * h[iy]) + d * h[knt];
                            h[hoffset] -= aa * bb;
                            h[iy] -= aa * tst;
                            h[knt] -= aa * ab;
                        }

                        if (b_k + 3 < i + 1) {
                            b_i = b_k + 2;
                        } else {
                            b_i = i;
                        }

                        for (j = 0; j <= b_i; j++) {
                            iy = j + 6 * (b_k - 1);
                            hoffset = j + 6 * b_k;
                            knt = j + 6 * (b_k + 1);
                            aa = (h[iy] + s * h[hoffset]) + d * h[knt];
                            h[iy] -= aa * bb;
                            h[hoffset] -= aa * tst;
                            h[knt] -= aa * ab;
                        }
                    } else {
                        if (nr == 2) {
                            for (j = b_k; j < 7; j++) {
                                iy = b_k + 6 * (j - 1);
                                hoffset = iy - 1;
                                aa = h[hoffset] + s * h[iy];
                                h[hoffset] -= aa * bb;
                                h[iy] -= aa * tst;
                            }

                            for (j = 0; j <= i; j++) {
                                iy = j + 6 * (b_k - 1);
                                hoffset = j + 6 * b_k;
                                aa = h[iy] + s * h[hoffset];
                                h[iy] -= aa * bb;
                                h[hoffset] -= aa * tst;
                            }
                        }
                    }
                }

                its++;
            }
        }

        if (!goto150) {
            info = i + 1;
            exitg1 = true;
        } else {
            if ((L != i + 1) && (L == i)) {
                b_i = i + 6 * i;
                hoffset = b_i - 1;
                s = h[hoffset];
                nr = 6 * (i - 1);
                knt = i + nr;
                d = h[knt];
                tst = h[b_i];
                xdlanv2(&h[(i + 6 * (i - 1)) - 1], &s, &d, &tst, &aa, &ab, &bb, &ba,
                        &h22, &rt1r);
                h[hoffset] = s;
                h[knt] = d;
                h[b_i] = tst;
                if (6 > i + 1) {
                    hoffset = 4 - i;
                    iy = i + (i + 1) * 6;
                    knt = iy - 1;
                    for (k = 0; k <= hoffset; k++) {
                        tst = h22 * h[knt] + rt1r * h[iy];
                        h[iy] = h22 * h[iy] - rt1r * h[knt];
                        h[knt] = tst;
                        iy += 6;
                        knt += 6;
                    }
                }

                if (i - 1 >= 1) {
                    iy = i * 6;
                    for (k = 0; k <= i - 2; k++) {
                        tst = h22 * h[nr] + rt1r * h[iy];
                        h[iy] = h22 * h[iy] - rt1r * h[nr];
                        h[nr] = tst;
                        iy++;
                        nr++;
                    }
                }
            }

            i = L - 2;
        }
    }

    return info;
}

//
// File trailer for xdhseqr.cpp
//
// [EOF]
//
