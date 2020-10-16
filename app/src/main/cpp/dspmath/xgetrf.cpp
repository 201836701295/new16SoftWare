//
// File: xgetrf.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 16-Oct-2020 18:58:30
//

// Include Files
#include "xgetrf.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include "slmfunc.h"
#include <cmath>
#include <string.h>

// Function Definitions

//
// Arguments    : double A[36]
//                int ipiv[6]
//                int *info
// Return Type  : void
//
void xgetrf(double A[36], int ipiv[6], int *info) {
    int i;
    int iy;
    int jA;
    int ix;
    for (i = 0; i < 6; i++) {
        ipiv[i] = i + 1;
    }

    *info = 0;
    for (int j = 0; j < 5; j++) {
        int mmj_tmp;
        int b;
        int jj;
        int jp1j;
        double smax;
        int k;
        mmj_tmp = 4 - j;
        b = j * 7;
        jj = j * 7;
        jp1j = b + 2;
        iy = 6 - j;
        jA = 0;
        ix = b;
        smax = std::abs(A[jj]);
        for (k = 2; k <= iy; k++) {
            double s;
            ix++;
            s = std::abs(A[ix]);
            if (s > smax) {
                jA = k - 1;
                smax = s;
            }
        }

        if (A[jj + jA] != 0.0) {
            if (jA != 0) {
                iy = j + jA;
                ipiv[j] = iy + 1;
                ix = j;
                for (k = 0; k < 6; k++) {
                    smax = A[ix];
                    A[ix] = A[iy];
                    A[iy] = smax;
                    ix += 6;
                    iy += 6;
                }
            }

            i = (jj - j) + 6;
            for (iy = jp1j; iy <= i; iy++) {
                A[iy - 1] /= A[jj];
            }
        } else {
            *info = j + 1;
        }

        iy = b + 6;
        jA = jj;
        for (b = 0; b <= mmj_tmp; b++) {
            smax = A[iy];
            if (A[iy] != 0.0) {
                ix = jj + 1;
                i = jA + 8;
                k = (jA - j) + 12;
                for (jp1j = i; jp1j <= k; jp1j++) {
                    A[jp1j - 1] += A[ix] * -smax;
                    ix++;
                }
            }

            iy += 6;
            jA += 6;
        }
    }

    if ((*info == 0) && (!(A[35] != 0.0))) {
        *info = 6;
    }
}

//
// File trailer for xgetrf.cpp
//
// [EOF]
//
