//
// File: computepsd.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 29-Sep-2020 18:02:36
//

// Include Files
#include "computepsd.h"
#include "FFTImplementationCallback.h"
#include "ifft.h"
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
// Arguments    : const coder::array<float, 2U> *Sxx1
//                const coder::array<double, 2U> *w2
//                const char range[8]
//                double nfft
//                double Fs
//                coder::array<float, 2U> *varargout_1
//                coder::array<double, 1U> *varargout_2
//                char varargout_3_data[]
//                int varargout_3_size[2]
// Return Type  : void
//
void computepsd(const coder::array<float, 2U> &Sxx1, const coder::array<double,
        2U> &w2, const char range[8], double nfft, double Fs, coder::
                array<float, 2U> &varargout_1, coder::array<double, 1U>
                &varargout_2, char varargout_3_data[], int varargout_3_size[2]) {
    int ret;
    static const char b[8] = {'o', 'n', 'e', 's', 'i', 'd', 'e', 'd'};

    coder::array<float, 2U> Sxx;
    int loop_ub;
    double r;
    int i;
    coder::array<double, 2U> b_select;
    static const char cv[10] = {'r', 'a', 'd', '/', 's', 'a', 'm', 'p', 'l', 'e'
    };

    coder::array<float, 2U> y;
    float varargin_3_data[1];
    float Sxx_unscaled_data[1];
    ret = memcmp(&range[0], &b[0], 8);
    if (ret == 0) {
        if (rtIsNaN(nfft) || rtIsInf(nfft)) {
            r = rtNaN;
        } else if (nfft == 0.0) {
            r = 0.0;
        } else {
            r = std::fmod(nfft, 2.0);
            if (r == 0.0) {
                r = 0.0;
            } else {
                if (nfft < 0.0) {
                    r += 2.0;
                }
            }
        }

        if (r == 1.0) {
            int i1;
            int result;
            boolean_T empty_non_axis_sizes;
            signed char input_sizes_idx_0;
            int sizes_idx_0;
            int b_input_sizes_idx_0;
            r = (nfft + 1.0) / 2.0;
            if (rtIsNaN(r)) {
                b_select.set_size(1, 1);
                b_select[0] = rtNaN;
            } else if (r < 1.0) {
                b_select.set_size(1, 0);
            } else if (rtIsInf(r) && (1.0 == r)) {
                b_select.set_size(1, 1);
                b_select[0] = rtNaN;
            } else {
                loop_ub = static_cast<int>(std::floor(r - 1.0));
                b_select.set_size(1, (loop_ub + 1));
                for (i = 0; i <= loop_ub; i++) {
                    b_select[i] = static_cast<double>(i) + 1.0;
                }
            }

            loop_ub = Sxx1.size(1);
            Sxx.set_size(b_select.size(1), Sxx1.size(1));
            for (i = 0; i < loop_ub; i++) {
                ret = b_select.size(1);
                for (i1 = 0; i1 < ret; i1++) {
                    Sxx[i1 + Sxx.size(0) * i] = Sxx1[(static_cast<int>(b_select[i1]) +
                                                      Sxx1.size(0) * i) - 1];
                }
            }

            if (2 > b_select.size(1)) {
                i = 0;
                i1 = 0;
            } else {
                i = 1;
                i1 = b_select.size(1);
            }

            loop_ub = Sxx1.size(1) - 1;
            ret = i1 - i;
            y.set_size(ret, Sxx1.size(1));
            for (i1 = 0; i1 <= loop_ub; i1++) {
                for (result = 0; result < ret; result++) {
                    y[result + y.size(0) * i1] = 2.0F * Sxx[(i + result) + Sxx.size(0) *
                                                                           i1];
                }
            }

            if (Sxx1.size(1) != 0) {
                result = Sxx1.size(1);
            } else if ((y.size(0) != 0) && (y.size(1) != 0)) {
                result = 1;
            } else {
                if (Sxx1.size(1) > 0) {
                    result = Sxx1.size(1);
                } else {
                    result = 0;
                }

                if (y.size(1) > result) {
                    result = 1;
                }
            }

            empty_non_axis_sizes = (result == 0);
            if (empty_non_axis_sizes || (Sxx1.size(1) != 0)) {
                input_sizes_idx_0 = 1;
            } else {
                input_sizes_idx_0 = 0;
            }

            if (empty_non_axis_sizes || ((y.size(0) != 0) && (y.size(1) != 0))) {
                sizes_idx_0 = y.size(0);
            } else {
                sizes_idx_0 = 0;
            }

            loop_ub = Sxx1.size(1) - 1;
            for (i = 0; i <= loop_ub; i++) {
                Sxx_unscaled_data[i] = Sxx[Sxx.size(0) * i];
            }

            b_input_sizes_idx_0 = input_sizes_idx_0;
            Sxx.set_size((input_sizes_idx_0 + sizes_idx_0), result);
            for (i = 0; i < result; i++) {
                for (i1 = 0; i1 < b_input_sizes_idx_0; i1++) {
                    Sxx[Sxx.size(0) * i] = Sxx_unscaled_data[input_sizes_idx_0 * i];
                }
            }

            for (i = 0; i < result; i++) {
                for (i1 = 0; i1 < sizes_idx_0; i1++) {
                    Sxx[(i1 + input_sizes_idx_0) + Sxx.size(0) * i] = y[i1 + sizes_idx_0 *
                                                                             i];
                }
            }
        } else {
            int i1;
            int result;
            boolean_T empty_non_axis_sizes;
            signed char input_sizes_idx_0;
            int sizes_idx_0;
            int b_input_sizes_idx_0;
            r = nfft / 2.0 + 1.0;
            if (rtIsNaN(r)) {
                b_select.set_size(1, 1);
                b_select[0] = rtNaN;
            } else if (r < 1.0) {
                b_select.set_size(1, 0);
            } else if (rtIsInf(r) && (1.0 == r)) {
                b_select.set_size(1, 1);
                b_select[0] = rtNaN;
            } else {
                loop_ub = static_cast<int>(std::floor(r - 1.0));
                b_select.set_size(1, (loop_ub + 1));
                for (i = 0; i <= loop_ub; i++) {
                    b_select[i] = static_cast<double>(i) + 1.0;
                }
            }

            loop_ub = Sxx1.size(1);
            Sxx.set_size(b_select.size(1), Sxx1.size(1));
            for (i = 0; i < loop_ub; i++) {
                ret = b_select.size(1);
                for (i1 = 0; i1 < ret; i1++) {
                    Sxx[i1 + Sxx.size(0) * i] = Sxx1[(static_cast<int>(b_select[i1]) +
                                                      Sxx1.size(0) * i) - 1];
                }
            }

            if (2 > b_select.size(1) - 1) {
                i = 0;
                i1 = 0;
            } else {
                i = 1;
                i1 = b_select.size(1) - 1;
            }

            loop_ub = Sxx1.size(1) - 1;
            ret = i1 - i;
            y.set_size(ret, Sxx1.size(1));
            for (i1 = 0; i1 <= loop_ub; i1++) {
                for (result = 0; result < ret; result++) {
                    y[result + y.size(0) * i1] = 2.0F * Sxx[(i + result) + Sxx.size(0) *
                                                                           i1];
                }
            }

            loop_ub = Sxx1.size(1) - 1;
            for (i = 0; i <= loop_ub; i++) {
                varargin_3_data[i] = Sxx[(b_select.size(1) + Sxx.size(0) * i) - 1];
            }

            if (Sxx1.size(1) != 0) {
                result = Sxx1.size(1);
            } else if ((y.size(0) != 0) && (y.size(1) != 0)) {
                result = 1;
            } else if (Sxx1.size(1) != 0) {
                result = Sxx1.size(1);
            } else {
                if (Sxx1.size(1) > 0) {
                    result = Sxx1.size(1);
                } else {
                    result = 0;
                }

                if (y.size(1) > result) {
                    result = 1;
                }

                if (Sxx1.size(1) > result) {
                    result = Sxx1.size(1);
                }
            }

            empty_non_axis_sizes = (result == 0);
            if (empty_non_axis_sizes || (Sxx1.size(1) != 0)) {
                input_sizes_idx_0 = 1;
            } else {
                input_sizes_idx_0 = 0;
            }

            if (empty_non_axis_sizes || ((y.size(0) != 0) && (y.size(1) != 0))) {
                b_input_sizes_idx_0 = y.size(0);
            } else {
                b_input_sizes_idx_0 = 0;
            }

            if (empty_non_axis_sizes || (Sxx1.size(1) != 0)) {
                sizes_idx_0 = 1;
            } else {
                sizes_idx_0 = 0;
            }

            loop_ub = Sxx1.size(1) - 1;
            for (i = 0; i <= loop_ub; i++) {
                Sxx_unscaled_data[i] = Sxx[Sxx.size(0) * i];
            }

            ret = input_sizes_idx_0;
            Sxx.set_size(((input_sizes_idx_0 + b_input_sizes_idx_0) + sizes_idx_0),
                         result);
            for (i = 0; i < result; i++) {
                for (i1 = 0; i1 < ret; i1++) {
                    Sxx[Sxx.size(0) * i] = Sxx_unscaled_data[input_sizes_idx_0 * i];
                }
            }

            for (i = 0; i < result; i++) {
                for (i1 = 0; i1 < b_input_sizes_idx_0; i1++) {
                    Sxx[(i1 + input_sizes_idx_0) + Sxx.size(0) * i] = y[i1 +
                                                                        b_input_sizes_idx_0 * i];
                }
            }

            for (i = 0; i < result; i++) {
                for (i1 = 0; i1 < sizes_idx_0; i1++) {
                    Sxx[(input_sizes_idx_0 + b_input_sizes_idx_0) + Sxx.size(0) * i] =
                            varargin_3_data[sizes_idx_0 * i];
                }
            }
        }

        varargout_2.set_size(b_select.size(1));
        loop_ub = b_select.size(1);
        for (i = 0; i < loop_ub; i++) {
            varargout_2[i] = w2[static_cast<int>(b_select[i]) - 1];
        }
    } else {
        Sxx.set_size(Sxx1.size(0), Sxx1.size(1));
        loop_ub = Sxx1.size(0) * Sxx1.size(1);
        for (i = 0; i < loop_ub; i++) {
            Sxx[i] = Sxx1[i];
        }

        varargout_2.set_size(w2.size(0));
        loop_ub = w2.size(0);
        for (i = 0; i < loop_ub; i++) {
            varargout_2[i] = w2[i];
        }
    }

    if (!rtIsNaN(Fs)) {
        varargout_1.set_size(Sxx.size(0), Sxx.size(1));
        loop_ub = Sxx.size(0) * Sxx.size(1);
        for (i = 0; i < loop_ub; i++) {
            varargout_1[i] = Sxx[i] / static_cast<float>(Fs);
        }

        varargout_3_size[0] = 1;
        varargout_3_size[1] = 2;
        varargout_3_data[0] = 'H';
        varargout_3_data[1] = 'z';
    } else {
        varargout_1.set_size(Sxx.size(0), Sxx.size(1));
        loop_ub = Sxx.size(0) * Sxx.size(1);
        for (i = 0; i < loop_ub; i++) {
            varargout_1[i] = Sxx[i] / 6.28318548F;
        }

        varargout_3_size[0] = 1;
        varargout_3_size[1] = 10;
        for (i = 0; i < 10; i++) {
            varargout_3_data[i] = cv[i];
        }
    }
}

//
// File trailer for computepsd.cpp
//
// [EOF]
//
