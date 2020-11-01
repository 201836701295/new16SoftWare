//
// File: octbank.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 01-Nov-2020 22:40:16
//

// Include Files
#include "octbank.h"
#include "aweight.h"
#include "butter.h"
#include "cweight.h"
#include "dspmath_rtwutil.h"
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
// OCT3BANK Simple one-third-octave filter bank.
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
// Arguments    : const coder::array<short, 2U> *x
//                double p[8]
//                double f[8]
// Return Type  : void
//
void octbank(const coder::array<short, 2U> &x, double p[8], double f[8]) {
    int y1_tmp;
    static const short iv[8] = {63, 125, 250, 500, 1000, 2000, 4000, 8000};

    int loop_ub;
    int i;
    int naxpy;
    int b_loop_ub;
    boolean_T idx[8];
    int k;
    double x_data[8];
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
    for (y1_tmp = 0; y1_tmp < 8; y1_tmp++) {
        f[y1_tmp] = iv[y1_tmp];
    }

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

        p[7 - i] = as / static_cast<double>(x.size(1));
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
        idx[i] = (p[i] > 0.0);
        if (p[i] > 0.0) {
            naxpy++;
            tmp_data[b_loop_ub] = static_cast<signed char>(i + 1);
            b_loop_ub++;
        }
    }

    for (y1_tmp = 0; y1_tmp < naxpy; y1_tmp++) {
        x_data[y1_tmp] = p[tmp_data[y1_tmp] - 1];
    }

    for (k = 0; k < naxpy; k++) {
        x_data[k] = std::log10(x_data[k]);
    }

    z1.set_size(1, naxpy);
    for (y1_tmp = 0; y1_tmp < naxpy; y1_tmp++) {
        z1[y1_tmp] = 10.0 * x_data[y1_tmp];
    }

    b_loop_ub = 0;
    for (i = 0; i < 8; i++) {
        if (p[i] > 0.0) {
            p[i] = z1[b_loop_ub];
            b_loop_ub++;
        }

        if (!idx[i]) {
            p[i] = rtNaN;
        }
    }

    //  Generate the plot
}

//
// File trailer for octbank.cpp
//
// [EOF]
//
