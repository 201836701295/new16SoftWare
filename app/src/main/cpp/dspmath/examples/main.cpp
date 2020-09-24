//
// File: main.cpp
//
// MATLAB Coder version            : 5.0
// C/C++ source code generated on  : 16-Sep-2020 10:10:07
//

//***********************************************************************
// This automatically generated example C++ main file shows how to call
// entry-point functions that MATLAB Coder generated. You must customize
// this file for your application. Do not modify this file directly.
// Instead, make a copy of this file, modify it, and integrate it into
// your development environment.
//
// This file initializes entry-point function arguments to a default
// size and value before calling the entry-point functions. It does
// not store or use any values returned from the entry-point functions.
// If necessary, it does pre-allocate memory for returned values.
// You can use this file as a starting point for a main function that
// you can deploy in your application.
//
// After you copy the file, and before you deploy it, you must make the
// following changes:
// * For variable-size function arguments, change the example sizes to
// the sizes that your application requires.
// * Change the example values of function arguments to the values that
// your application requires.
// * If the entry-point functions return values, store these values or
// otherwise use them as required by your application.
//
//***********************************************************************

// Include Files
#include "main.h"
#include "dspmath_terminate.h"
#include "ifft.h"
#include "mconv.h"
#include "mfft.h"
#include "mifft.h"
#include "mywelch.h"
#include "rt_nonfinite.h"
#include <string.h>

// Function Declarations
static coder::array<creal32_T, 2U> argInit_1xUnbounded_creal32_T();

static coder::array<float, 2U> argInit_1xUnbounded_real32_T();

static creal32_T argInit_creal32_T();

static int argInit_int32_T();

static float argInit_real32_T();

static void main_mconv();

static void main_mfft();

static void main_mifft();

static void main_mywelch();

// Function Definitions

//
// Arguments    : void
// Return Type  : coder::array<creal32_T, 2U>
//
static coder::array<creal32_T, 2U> argInit_1xUnbounded_creal32_T() {
    coder::array<creal32_T, 2U> result;

    // Set the size of the array.
    // Change this size to the value that the application requires.
    result.set_size(1, 2);

    // Loop over the array to initialize each element.
    for (int idx1 = 0; idx1 < result.size(1); idx1++) {
        // Set the value of the array element.
        // Change this value to the value that the application requires.
        result[idx1] = argInit_creal32_T();
    }

    return result;
}

//
// Arguments    : void
// Return Type  : coder::array<float, 2U>
//
static coder::array<float, 2U> argInit_1xUnbounded_real32_T() {
    coder::array<float, 2U> result;

    // Set the size of the array.
    // Change this size to the value that the application requires.
    result.set_size(1, 2);

    // Loop over the array to initialize each element.
    for (int idx1 = 0; idx1 < result.size(1); idx1++) {
        // Set the value of the array element.
        // Change this value to the value that the application requires.
        result[idx1] = argInit_real32_T();
    }

    return result;
}

//
// Arguments    : void
// Return Type  : creal32_T
//
static creal32_T argInit_creal32_T() {
    creal32_T result;
    float re_tmp;

    // Set the value of the complex variable.
    // Change this value to the value that the application requires.
    re_tmp = argInit_real32_T();
    result.re = re_tmp;
    result.im = re_tmp;
    return result;
}

//
// Arguments    : void
// Return Type  : int
//
static int argInit_int32_T() {
    return 0;
}

//
// Arguments    : void
// Return Type  : float
//
static float argInit_real32_T() {
    return 0.0F;
}

//
// Arguments    : void
// Return Type  : void
//
static void main_mconv() {
    coder::array<float, 2U> a_tmp;
    coder::array<creal32_T, 2U> c;

    // Initialize function 'mconv' input arguments.
    // Initialize function input argument 'a'.
    a_tmp = argInit_1xUnbounded_real32_T();

    // Initialize function input argument 'b'.
    // Call the entry-point 'mconv'.
    mconv(a_tmp, a_tmp, argInit_int32_T(), c);
}

//
// Arguments    : void
// Return Type  : void
//
static void main_mfft() {
    coder::array<float, 2U> X;
    coder::array<creal32_T, 2U> F;

    // Initialize function 'mfft' input arguments.
    // Initialize function input argument 'X'.
    X = argInit_1xUnbounded_real32_T();

    // Call the entry-point 'mfft'.
    mfft(X, argInit_int32_T(), F);
}

//
// Arguments    : void
// Return Type  : void
//
static void main_mifft() {
    coder::array<creal32_T, 2U> F;
    coder::array<creal32_T, 2U> X;

    // Initialize function 'mifft' input arguments.
    // Initialize function input argument 'F'.
    F = argInit_1xUnbounded_creal32_T();

    // Call the entry-point 'mifft'.
    mifft(F, argInit_int32_T(), X);
}

//
// Arguments    : void
// Return Type  : void
//
static void main_mywelch() {
    coder::array<float, 2U> x;
    int N_tmp;
    coder::array<float, 2U> pxx;
    coder::array<float, 1U> f;

    // Initialize function 'mywelch' input arguments.
    // Initialize function input argument 'x'.
    x = argInit_1xUnbounded_real32_T();
    N_tmp = argInit_int32_T();

    // Call the entry-point 'mywelch'.
    mywelch(x, N_tmp, N_tmp, pxx, f);
}

//
// Arguments    : int argc
//                const char * const argv[]
// Return Type  : int
//
int main(int, const char *const[]) {
    // The initialize function is being called automatically from your entry-point function. So, a call to initialize is not included here.
    // Invoke the entry-point functions.
    // You can call entry-point functions multiple times.
    main_mconv();
    main_mfft();
    main_mifft();
    main_mywelch();

    // Terminate the application.
    // You do not need to do this more than one time.
    dspmath_terminate();
    return 0;
}

//
// File trailer for main.cpp
//
// [EOF]
//
