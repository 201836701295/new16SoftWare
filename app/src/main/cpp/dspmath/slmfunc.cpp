//
// File: slmfunc.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 15-Oct-2020 21:35:42
//

// Include Files
#include "slmfunc.h"
#include "FFTImplementationCallback.h"
#include "butter.h"
#include "computepsd.h"
#include "dspmath_data.h"
#include "dspmath_initialize.h"
#include "ifft.h"
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
// Arguments    : const coder::array<float, 2U> *x
//                float L8[8]
//                float F[8]
//                float *LA
// Return Type  : void
//
void slmfunc(const coder::array<float, 2U> &x, float L8[8], float F[8], float
*LA) {
    int loop_ub;
    int i;
    int xblockoffset;
    int naxpy;
    int nblocks;
    boolean_T idx[8];
    double b_L8[8];
    int k;
    double b_LA[8];
    signed char tmp_data[8];
    double Fc_tmp;
    static const double dv[8] = {62.5, 125.0, 250.0, 500.0, 1000.0, 2000.0,
                                 4000.0, 8000.0};

    coder::array<double, 2U> y;
    double Fc[2];
    double B[7];
    double A[7];
    coder::array<float, 1U> b;
    coder::array<float, 1U> b_y1;
    static const double Aweight[8] = {-26.2, -16.1, -8.6, -3.2, 0.0, 1.2, 1.0,
                                      -1.1};

    static const short b_F[8] = {63, 125, 250, 500, 1000, 2000, 4000, 8000};

    coder::array<float, 2U> z1;
    float as;
    float bsum;
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
        int lastBlockLength;

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
        Fc_tmp = dv[7 - i] / 22050.0;
        Fc[0] = Fc_tmp * 0.70710678118654757 / 0.98505216233236492;
        Fc[1] = Fc_tmp * 1.4142135623730951 * 0.98505216233236492;
        butter(Fc, B, A);
        b.set_size(x.size(1));
        for (nblocks = 0; nblocks < loop_ub; nblocks++) {
            b[nblocks] = x[nblocks];
        }

        nx = b.size(0) - 1;
        naxpy = b.size(0);
        b_y1.set_size(b.size(0));
        for (nblocks = 0; nblocks < naxpy; nblocks++) {
            b_y1[nblocks] = 0.0F;
        }

        for (k = 0; k <= nx; k++) {
            xblockoffset = nx - k;
            naxpy = xblockoffset + 1;
            if (naxpy >= 7) {
                naxpy = 7;
            }

            for (lastBlockLength = 0; lastBlockLength < naxpy; lastBlockLength++) {
                nblocks = k + lastBlockLength;
                b_y1[nblocks] = b_y1[nblocks] + b[k] * static_cast<float>
                (B[lastBlockLength]);
            }

            if (xblockoffset < 6) {
                naxpy = xblockoffset;
            } else {
                naxpy = 6;
            }

            as = -b_y1[k];
            for (lastBlockLength = 0; lastBlockLength < naxpy; lastBlockLength++) {
                nblocks = (k + lastBlockLength) + 1;
                b_y1[nblocks] = b_y1[nblocks] + as * static_cast<float>
                (A[lastBlockLength + 1]);
            }
        }

        nblocks = b_y1.size(0);
        z1.set_size(1, b_y1.size(0));
        for (k = 0; k < nblocks; k++) {
            z1[k] = b_y1[k] * b_y1[k];
        }

        if (z1.size(1) == 0) {
            as = 0.0F;
        } else {
            if (z1.size(1) <= 1024) {
                naxpy = z1.size(1);
                lastBlockLength = 0;
                nblocks = 1;
            } else {
                naxpy = 1024;
                nblocks = z1.size(1) / 1024;
                lastBlockLength = z1.size(1) - (nblocks << 10);
                if (lastBlockLength > 0) {
                    nblocks++;
                } else {
                    lastBlockLength = 1024;
                }
            }

            as = z1[0];
            for (k = 2; k <= naxpy; k++) {
                as += z1[k - 1];
            }

            for (nx = 2; nx <= nblocks; nx++) {
                xblockoffset = (nx - 1) << 10;
                bsum = z1[xblockoffset];
                if (nx == nblocks) {
                    naxpy = lastBlockLength;
                } else {
                    naxpy = 1024;
                }

                for (k = 2; k <= naxpy; k++) {
                    bsum += z1[(xblockoffset + k) - 1];
                }

                as += bsum;
            }
        }

        b_L8[7 - i] = as / static_cast<float>(x.size(1));
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
    xblockoffset = 0;
    naxpy = 0;
    for (i = 0; i < 8; i++) {
        idx[i] = (b_L8[i] > 0.0);
        if (b_L8[i] > 0.0) {
            xblockoffset++;
            tmp_data[naxpy] = static_cast<signed char>(i + 1);
            naxpy++;
        }
    }

    for (nblocks = 0; nblocks < xblockoffset; nblocks++) {
        b_LA[nblocks] = b_L8[tmp_data[nblocks] - 1];
    }

    for (k = 0; k < xblockoffset; k++) {
        b_LA[k] = std::log10(b_LA[k]);
    }

    y.set_size(1, xblockoffset);
    for (nblocks = 0; nblocks < xblockoffset; nblocks++) {
        y[nblocks] = 10.0 * b_LA[nblocks];
    }

    naxpy = 0;

    //  Generate the plot
    for (k = 0; k < 8; k++) {
        Fc_tmp = b_L8[k];
        if (b_L8[k] > 0.0) {
            double d;
            Fc_tmp = y[naxpy];
            d = y[naxpy];
            b_L8[k] = d;
            naxpy++;
        }

        if (!idx[k]) {
            Fc_tmp = rtNaN;
            b_L8[k] = rtNaN;
        }

        b_LA[k] = rt_powd_snf(10.0, 0.1 * (Fc_tmp + Aweight[k]));
    }

    Fc_tmp = b_LA[0];
    for (k = 0; k < 7; k++) {
        Fc_tmp += b_LA[k + 1];
    }

    *LA = static_cast<float>(10.0 * std::log10(Fc_tmp));
    for (nblocks = 0; nblocks < 8; nblocks++) {
        L8[nblocks] = static_cast<float>(b_L8[nblocks]);
        F[nblocks] = b_F[nblocks];
    }
}

//
// File trailer for slmfunc.cpp
//
// [EOF]
//
