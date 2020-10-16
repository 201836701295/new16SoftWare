//
// File: slmfunc.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 16-Oct-2020 18:58:30
//

// Include Files
#include "slmfunc.h"
#include "butter.h"
#include "computepsd.h"
#include "dspmath_data.h"
#include "dspmath_initialize.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "welch.h"
#include <cmath>
#include <math.h>
#include <string.h>

// Function Declarations
static double rt_powd_snf(double u0, double u1);

// Function Definitions

//
// Arguments    : double u0
//                double u1
// Return Type  : double
//
static double rt_powd_snf(double u0, double u1) {
    double y;
    if (rtIsNaN(u0) || rtIsNaN(u1)) {
        y = rtNaN;
    } else {
        double d;
        double d1;
        d = std::abs(u0);
        d1 = std::abs(u1);
        if (rtIsInf(u1)) {
            if (d == 1.0) {
                y = 1.0;
            } else if (d > 1.0) {
                if (u1 > 0.0) {
                    y = rtInf;
                } else {
                    y = 0.0;
                }
            } else if (u1 > 0.0) {
                y = 0.0;
            } else {
                y = rtInf;
            }
        } else if (d1 == 0.0) {
            y = 1.0;
        } else if (d1 == 1.0) {
            if (u1 > 0.0) {
                y = u0;
            } else {
                y = 1.0 / u0;
            }
        } else if (u1 == 2.0) {
            y = u0 * u0;
        } else if ((u1 == 0.5) && (u0 >= 0.0)) {
            y = std::sqrt(u0);
        } else if ((u0 < 0.0) && (u1 > std::floor(u1))) {
            y = rtNaN;
        } else {
            y = pow(u0, u1);
        }
    }

    return y;
}

//
// Arguments    : const coder::array<short, 2U> *x
//                float p[8]
//                float ff[8]
//                float *Lp
// Return Type  : void
//
void slmfunc(const coder::array<short, 2U> &x, float p[8], float ff[8], float
*Lp) {
    int loop_ub;
    int i;
    int naxpy;
    int b_loop_ub;
    int y1_tmp;
    boolean_T idx[8];
    double b_p[8];
    int k;
    double LA[8];
    signed char tmp_data[8];
    double as;
    static const double dv[8] = {62.5, 125.0, 250.0, 500.0, 1000.0, 2000.0,
                                 4000.0, 8000.0};

    coder::array<double, 2U> z1;
    double Fc[2];
    double B[7];
    double A[7];
    coder::array<double, 1U> b;
    coder::array<double, 1U> b_y1;
    static const double Aweight[8] = {-26.2, -16.1, -8.6, -3.2, 0.0, 1.2, 1.0,
                                      -1.1};

    static const short F[8] = {63, 125, 250, 500, 1000, 2000, 4000, 8000};

    if (!isInitialized_dspmath) {
        dspmath_initialize();
    }

    //  OCT3BANK Simple one-third-octave filter bank.
    //     OCT3BANK(X) plots one-third-octave power spectra of signal vector X.
    //     Implementation based on ANSI S1.11-1986 Order-3 filters.
    //     Sampling frequency Fs = 44100 Hz. Restricted one-third-octave-band
    //     range (from 100 Hz to 5000 Hz). RMS power is computed in each band
    //     and expressed in dB with 1 as reference level.
    //
    //     [P,F] = OCT3BANK(X) returns two length-18 row-vectors with
    //     the RMS power (in dB) in P and the corresponding preferred labeling
    //     frequencies (ANSI S1.6-1984) in F.
    //
    //     See also OCT3DSGN, OCT3SPEC, OCTDSGN, OCTSPEC.
    //  Author: Christophe Couvreur, Faculte Polytechnique de Mons (Belgium)
    //          couvreur@thor.fpms.ac.be
    //  Last modification: Aug. 23, 1997, 10:30pm.
    //  References:
    //     [1] ANSI S1.1-1986 (ASA 65-1986): Specifications for
    //         Octave-Band and Fractional-Octave-Band Analog and
    //         Digital Filters, 1993.
    //     [2] S. J. Orfanidis, Introduction to Signal Processing,
    //         Prentice Hall, Englewood Cliffs, 1996.
    // Fs = 44100; 				% Sampling Frequency
    //  Order of analysis filters.
    //  Preferred labeling freq.
    //  Exact center freq.
    //  Design filters and compute RMS powers in 1/1-oct. bands
    //  5000 Hz band to 1600 Hz band, direct implementation of filters.
    loop_ub = x.size(1);
    for (i = 0; i < 8; i++) {
        int nx;

        //  OCTDSGN  Design of an octave filter.
        //     [B,A] = OCTDSGN(Fc,Fs,N) designs a digital octave filter with
        //     center frequency Fc for sampling frequency Fs.
        //     The filter are designed according to the Order-N specification
        //     of the ANSI S1.1-1986 standard. Default value for N is 3.
        //     Warning: for meaningful design results, center values used
        //     should preferably be in range Fs/200 < Fc < Fs/5.
        //     Usage of the filter: Y = FILTER(B,A,X).
        //
        //     Requires the Signal Processing Toolbox.
        //
        //     See also OCTSPEC, OCT3DSGN, OCT3SPEC.
        //  Author: Christophe Couvreur, Faculte Polytechnique de Mons (Belgium)
        //          couvreur@thor.fpms.ac.be
        //  Last modification: Aug. 22, 1997, 9:00pm.
        //  References:
        //     [1] ANSI S1.1-1986 (ASA 65-1986): Specifications for
        //         Octave-Band and Fractional-Octave-Band Analog and
        //         Digital Filters, 1993.
        //  Design Butterworth 2Nth-order octave filter
        //  Note: BUTTER is based on a bilinear transformation, as suggested in [1].
        // W1 = Fc/(Fs/2)*sqrt(1/2);
        // W2 = Fc/(Fs/2)*sqrt(2);
        as = dv[7 - i] / 22050.0;
        Fc[0] = as * 0.70710678118654757 / 0.98505216233236492;
        Fc[1] = as * 1.4142135623730951 * 0.98505216233236492;
        butter(Fc, B, A);
        b.set_size(x.size(1));
        for (y1_tmp = 0; y1_tmp < loop_ub; y1_tmp++) {
            b[y1_tmp] = x[y1_tmp];
        }

        nx = b.size(0) - 1;
        b_loop_ub = b.size(0);
        b_y1.set_size(b.size(0));
        for (y1_tmp = 0; y1_tmp < b_loop_ub; y1_tmp++) {
            b_y1[y1_tmp] = 0.0;
        }

        for (k = 0; k <= nx; k++) {
            int j;
            b_loop_ub = nx - k;
            naxpy = b_loop_ub + 1;
            if (naxpy >= 7) {
                naxpy = 7;
            }

            for (j = 0; j < naxpy; j++) {
                y1_tmp = k + j;
                b_y1[y1_tmp] = b_y1[y1_tmp] + b[k] * B[j];
            }

            if (b_loop_ub < 6) {
                naxpy = b_loop_ub;
            } else {
                naxpy = 6;
            }

            as = -b_y1[k];
            for (j = 0; j < naxpy; j++) {
                y1_tmp = (k + j) + 1;
                b_y1[y1_tmp] = b_y1[y1_tmp] + as * A[j + 1];
            }
        }

        y1_tmp = b_y1.size(0);
        z1.set_size(1, b_y1.size(0));
        for (k = 0; k < y1_tmp; k++) {
            z1[k] = rt_powd_snf(b_y1[k], 2.0);
        }

        y1_tmp = z1.size(1);
        if (z1.size(1) == 0) {
            as = 0.0;
        } else {
            as = z1[0];
            for (k = 2; k <= y1_tmp; k++) {
                as += z1[k - 1];
            }
        }

        b_p[7 - i] = as / static_cast<double>(x.size(1));
    }

    //  1250 Hz to 100 Hz, multirate filter implementation (see [2]).
    // [Bu,Au] = oct3dsgn(ff(18),Fs,N); 	% Upper 1/3-oct. band in last octave.
    // [Bc,Ac] = oct3dsgn(ff(17),Fs,N); 	% Center 1/3-oct. band in last octave.
    // [Bl,Al] = oct3dsgn(ff(16),Fs,N); 	% Lower 1/3-oct. band in last octave.
    // for j = 4:-1:0
    //    x = decimate(x,2);
    //    m = length(x);
    //    y = filter(Bu,Au,x);
    //    P(j*3+3) = sum(y.^2)/m;
    //    y = filter(Bc,Ac,x);
    //    P(j*3+2) = sum(y.^2)/m;
    //    y = filter(Bl,Al,x);
    //    P(j*3+1) = sum(y.^2)/m;
    // end
    //  Convert to decibels.
    //  Reference level for dB scale.
    naxpy = 0;
    b_loop_ub = 0;
    for (i = 0; i < 8; i++) {
        idx[i] = (b_p[i] > 0.0);
        if (b_p[i] > 0.0) {
            naxpy++;
            tmp_data[b_loop_ub] = static_cast<signed char>(i + 1);
            b_loop_ub++;
        }
    }

    for (y1_tmp = 0; y1_tmp < naxpy; y1_tmp++) {
        LA[y1_tmp] = b_p[tmp_data[y1_tmp] - 1];
    }

    for (k = 0; k < naxpy; k++) {
        LA[k] = std::log10(LA[k]);
    }

    z1.set_size(1, naxpy);
    for (y1_tmp = 0; y1_tmp < naxpy; y1_tmp++) {
        z1[y1_tmp] = 10.0 * LA[y1_tmp];
    }

    b_loop_ub = 0;

    //  Generate the plot
    for (k = 0; k < 8; k++) {
        as = b_p[k];
        if (b_p[k] > 0.0) {
            double d;
            as = z1[b_loop_ub];
            d = z1[b_loop_ub];
            b_p[k] = d;
            b_loop_ub++;
        }

        if (!idx[k]) {
            as = rtNaN;
            b_p[k] = rtNaN;
        }

        LA[k] = rt_powd_snf(10.0, 0.1 * (as + Aweight[k]));
    }

    as = LA[0];
    for (k = 0; k < 7; k++) {
        as += LA[k + 1];
    }

    for (y1_tmp = 0; y1_tmp < 8; y1_tmp++) {
        p[y1_tmp] = static_cast<float>(b_p[y1_tmp]);
        ff[y1_tmp] = F[y1_tmp];
    }

    *Lp = static_cast<float>(10.0 * std::log10(as));
}

//
// File trailer for slmfunc.cpp
//
// [EOF]
//
