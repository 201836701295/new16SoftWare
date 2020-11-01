//
// File: welch.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "welch.h"
#include "FFTImplementationCallback.h"
#include "aweight.h"
#include "computeperiodogram.h"
#include "computepsd.h"
#include "cweight.h"
#include "ifft.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "octbank.h"
#include "psdfreqvec.h"
#include "rt_nonfinite.h"
#include "welchparse.h"
#include "zweight.h"
#include <cmath>
#include <string.h>

// Function Definitions

//
// Arguments    : const coder::array<float, 2U> *x
//                int varargin_3
//                int varargin_4
//                coder::array<float, 2U> *varargout_1
//                coder::array<float, 1U> *varargout_2
// Return Type  : void
//
void welch(const coder::array<float, 2U> &x, int varargin_3, int varargin_4,
           coder::array<float, 2U> &varargout_1, coder::array<float, 1U>
           &varargout_2) {
    coder::array<float, 1U> x1;
    double ndbl;
    coder::array<double, 1U> win1;
    double noverlap;
    double k1;
    double L;
    struct_T options;
    double options_nfft;
    double options_Fs;
    int i;
    coder::array<float, 1U> win;
    char options_range[8];
    int nm1d2;
    double LminusOverlap;
    coder::array<double, 2U> xStart;
    coder::array<double, 2U> xEnd;
    int n;
    coder::array<float, 2U> Sxx1;
    int k;
    coder::array<double, 1U> w;
    char units_data[10];
    int units_size[2];
    coder::array<float, 1U> b_x1;
    coder::array<float, 1U> Sxxk;
    coder::array<float, 1U> b_Sxx1;
    coder::array<float, 1U> c_Sxx1;
    welchparse(x, varargin_3, varargin_4, x1, &ndbl, win1, &noverlap, &k1, &L,
               &options);
    options_nfft = options.nfft;
    options_Fs = options.Fs;
    for (i = 0; i < 8; i++) {
        options_range[i] = options.range[i];
    }

    win.set_size(win1.size(0));
    nm1d2 = win1.size(0);
    for (i = 0; i < nm1d2; i++) {
        win[i] = static_cast<float>(win1[i]);
    }

    LminusOverlap = L - noverlap;
    noverlap = k1 * LminusOverlap;
    if (rtIsNaN(LminusOverlap) || rtIsNaN(noverlap)) {
        xStart.set_size(1, 1);
        xStart[0] = rtNaN;
    } else if ((LminusOverlap == 0.0) || ((1.0 < noverlap) && (LminusOverlap < 0.0))
               || ((noverlap < 1.0) && (LminusOverlap > 0.0))) {
        xStart.set_size(1, 0);
    } else if (rtIsInf(noverlap) && (rtIsInf(LminusOverlap) || (1.0 == noverlap))) {
        xStart.set_size(1, 1);
        xStart[0] = rtNaN;
    } else if (rtIsInf(LminusOverlap)) {
        xStart.set_size(1, 1);
        xStart[0] = 1.0;
    } else if (std::floor(LminusOverlap) == LminusOverlap) {
        nm1d2 = static_cast<int>(std::floor((noverlap - 1.0) / LminusOverlap));
        xStart.set_size(1, (nm1d2 + 1));
        for (i = 0; i <= nm1d2; i++) {
            xStart[i] = LminusOverlap * static_cast<double>(i) + 1.0;
        }
    } else {
        double apnd;
        double cdiff;
        double u1;
        ndbl = std::floor((noverlap - 1.0) / LminusOverlap + 0.5);
        apnd = ndbl * LminusOverlap + 1.0;
        if (LminusOverlap > 0.0) {
            cdiff = apnd - noverlap;
        } else {
            cdiff = noverlap - apnd;
        }

        u1 = std::abs(noverlap);
        if ((1.0 > u1) || rtIsNaN(u1)) {
            u1 = 1.0;
        }

        if (std::abs(cdiff) < 4.4408920985006262E-16 * u1) {
            ndbl++;
            apnd = noverlap;
        } else if (cdiff > 0.0) {
            apnd = (ndbl - 1.0) * LminusOverlap + 1.0;
        } else {
            ndbl++;
        }

        if (ndbl >= 0.0) {
            n = static_cast<int>(ndbl);
        } else {
            n = 0;
        }

        xStart.set_size(1, n);
        if (n > 0) {
            xStart[0] = 1.0;
            if (n > 1) {
                xStart[n - 1] = apnd;
                nm1d2 = (n - 1) / 2;
                for (k = 0; k <= nm1d2 - 2; k++) {
                    ndbl = (static_cast<double>(k) + 1.0) * LminusOverlap;
                    xStart[k + 1] = ndbl + 1.0;
                    xStart[(n - k) - 2] = apnd - ndbl;
                }

                if (nm1d2 << 1 == n - 1) {
                    xStart[nm1d2] = (apnd + 1.0) / 2.0;
                } else {
                    ndbl = static_cast<double>(nm1d2) * LminusOverlap;
                    xStart[nm1d2] = ndbl + 1.0;
                    xStart[nm1d2 + 1] = apnd - ndbl;
                }
            }
        }
    }

    xEnd.set_size(1, xStart.size(1));
    nm1d2 = xStart.size(0) * xStart.size(1);
    for (i = 0; i < nm1d2; i++) {
        xEnd[i] = (xStart[i] + L) - 1.0;
    }

    if (options.maxhold) {
        n = static_cast<int>(options.nfft);
        Sxx1.set_size(0, 0);
        i = static_cast<int>(k1);
        for (int ii = 0; ii < i; ii++) {
            int i1;
            ndbl = xStart[ii];
            noverlap = xEnd[ii];
            if (ndbl > noverlap) {
                i1 = 0;
                k = 0;
            } else {
                i1 = static_cast<int>(ndbl) - 1;
                k = static_cast<int>(noverlap);
            }

            nm1d2 = k - i1;
            b_x1.set_size(nm1d2);
            for (k = 0; k < nm1d2; k++) {
                b_x1[k] = x1[i1 + k];
            }

            computeperiodogram(b_x1, win, options_nfft, options_Fs, Sxxk, win1);
            if (ii + 1U == 1U) {
                nm1d2 = Sxxk.size(0);
                for (i1 = 0; i1 < nm1d2; i1++) {
                    Sxxk[i1] = static_cast<float>(k1) * Sxxk[i1];
                }

                if (n <= Sxxk.size(0)) {
                    i1 = n;
                } else {
                    i1 = Sxxk.size(0);
                }

                c_Sxx1.set_size(i1);
                for (k = 0; k < i1; k++) {
                    if (rtIsNaNF(Sxxk[k])) {
                        c_Sxx1[k] = rtMinusInfF;
                    } else {
                        c_Sxx1[k] = Sxxk[k];
                    }
                }

                nm1d2 = c_Sxx1.size(0);
                Sxx1.set_size(c_Sxx1.size(0), 1);
                for (i1 = 0; i1 < nm1d2; i1++) {
                    Sxx1[i1] = c_Sxx1[i1];
                }
            } else {
                nm1d2 = Sxxk.size(0);
                for (i1 = 0; i1 < nm1d2; i1++) {
                    Sxxk[i1] = static_cast<float>(k1) * Sxxk[i1];
                }

                if (Sxx1.size(0) <= Sxxk.size(0)) {
                    i1 = Sxx1.size(0);
                } else {
                    i1 = Sxxk.size(0);
                }

                b_Sxx1.set_size(i1);
                for (k = 0; k < i1; k++) {
                    if ((Sxx1[k] > Sxxk[k]) || rtIsNaNF(Sxxk[k])) {
                        b_Sxx1[k] = Sxx1[k];
                    } else {
                        b_Sxx1[k] = Sxxk[k];
                    }
                }

                nm1d2 = b_Sxx1.size(0);
                Sxx1.set_size(b_Sxx1.size(0), 1);
                for (i1 = 0; i1 < nm1d2; i1++) {
                    Sxx1[i1] = b_Sxx1[i1];
                }
            }
        }

        nm1d2 = Sxx1.size(0) * Sxx1.size(1);
        for (i = 0; i < nm1d2; i++) {
            Sxx1[i] = Sxx1[i] / static_cast<float>(k1);
        }

        psdfreqvec(options.nfft, options.Fs, win1);
        n = win1.size(0);
        xStart = win1.reshape(n, 1);
        computepsd(Sxx1, xStart, options_range, options.nfft, options.Fs,
                   varargout_1, w, units_data, units_size);
    } else if (options.minhold) {
        Sxx1.set_size(0, 0);
        i = static_cast<int>(k1);
        for (int ii = 0; ii < i; ii++) {
            int i1;
            ndbl = xStart[ii];
            noverlap = xEnd[ii];
            if (ndbl > noverlap) {
                i1 = 0;
                k = 0;
            } else {
                i1 = static_cast<int>(ndbl) - 1;
                k = static_cast<int>(noverlap);
            }

            nm1d2 = k - i1;
            b_x1.set_size(nm1d2);
            for (k = 0; k < nm1d2; k++) {
                b_x1[k] = x1[i1 + k];
            }

            computeperiodogram(b_x1, win, options_nfft, options_Fs, Sxxk, win1);
            if (ii + 1U == 1U) {
                nm1d2 = Sxxk.size(0);
                for (i1 = 0; i1 < nm1d2; i1++) {
                    Sxxk[i1] = static_cast<float>(k1) * Sxxk[i1];
                }

                if (static_cast<int>(options_nfft) <= Sxxk.size(0)) {
                    i1 = static_cast<int>(options_nfft);
                } else {
                    i1 = Sxxk.size(0);
                }

                c_Sxx1.set_size(i1);
                for (k = 0; k < i1; k++) {
                    if (rtIsNaNF(Sxxk[k])) {
                        c_Sxx1[k] = rtInfF;
                    } else {
                        c_Sxx1[k] = Sxxk[k];
                    }
                }

                nm1d2 = c_Sxx1.size(0);
                Sxx1.set_size(c_Sxx1.size(0), 1);
                for (i1 = 0; i1 < nm1d2; i1++) {
                    Sxx1[i1] = c_Sxx1[i1];
                }
            } else {
                nm1d2 = Sxxk.size(0);
                for (i1 = 0; i1 < nm1d2; i1++) {
                    Sxxk[i1] = static_cast<float>(k1) * Sxxk[i1];
                }

                if (Sxx1.size(0) <= Sxxk.size(0)) {
                    i1 = Sxx1.size(0);
                } else {
                    i1 = Sxxk.size(0);
                }

                b_Sxx1.set_size(i1);
                for (k = 0; k < i1; k++) {
                    if ((Sxx1[k] < Sxxk[k]) || rtIsNaNF(Sxxk[k])) {
                        b_Sxx1[k] = Sxx1[k];
                    } else {
                        b_Sxx1[k] = Sxxk[k];
                    }
                }

                nm1d2 = b_Sxx1.size(0);
                Sxx1.set_size(b_Sxx1.size(0), 1);
                for (i1 = 0; i1 < nm1d2; i1++) {
                    Sxx1[i1] = b_Sxx1[i1];
                }
            }
        }

        nm1d2 = Sxx1.size(0) * Sxx1.size(1);
        for (i = 0; i < nm1d2; i++) {
            Sxx1[i] = Sxx1[i] / static_cast<float>(k1);
        }

        psdfreqvec(options.nfft, options.Fs, win1);
        n = win1.size(0);
        xStart = win1.reshape(n, 1);
        computepsd(Sxx1, xStart, options_range, options.nfft, options.Fs,
                   varargout_1, w, units_data, units_size);
    } else {
        Sxx1.set_size(0, 0);
        i = static_cast<int>(k1);
        for (int ii = 0; ii < i; ii++) {
            int i1;
            ndbl = xStart[ii];
            noverlap = xEnd[ii];
            if (ndbl > noverlap) {
                i1 = 0;
                k = 0;
            } else {
                i1 = static_cast<int>(ndbl) - 1;
                k = static_cast<int>(noverlap);
            }

            nm1d2 = k - i1;
            b_x1.set_size(nm1d2);
            for (k = 0; k < nm1d2; k++) {
                b_x1[k] = x1[i1 + k];
            }

            computeperiodogram(b_x1, win, options_nfft, options_Fs, Sxxk, win1);
            if (ii + 1U == 1U) {
                nm1d2 = static_cast<int>(options_nfft);
                Sxx1.set_size(nm1d2, 1);
                for (i1 = 0; i1 < nm1d2; i1++) {
                    Sxx1[i1] = Sxxk[i1];
                }
            } else {
                nm1d2 = Sxx1.size(0);
                Sxxk.set_size(Sxx1.size(0));
                n = Sxx1.size(0);
                for (i1 = 0; i1 < n; i1++) {
                    Sxxk[i1] = Sxx1[i1] + Sxxk[i1];
                }

                n = Sxx1.size(0);
                Sxx1.set_size(n, 1);
                for (i1 = 0; i1 < nm1d2; i1++) {
                    Sxx1[i1] = Sxxk[i1];
                }
            }
        }

        nm1d2 = Sxx1.size(0) * Sxx1.size(1);
        for (i = 0; i < nm1d2; i++) {
            Sxx1[i] = Sxx1[i] / static_cast<float>(k1);
        }

        psdfreqvec(options.nfft, options.Fs, win1);
        n = win1.size(0);
        xStart = win1.reshape(n, 1);
        computepsd(Sxx1, xStart, options_range, options.nfft, options.Fs,
                   varargout_1, w, units_data, units_size);
    }

    varargout_2.set_size(w.size(0));
    nm1d2 = w.size(0);
    for (i = 0; i < nm1d2; i++) {
        varargout_2[i] = static_cast<float>(w[i]);
    }
}

//
// File trailer for welch.cpp
//
// [EOF]
//
