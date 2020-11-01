package edu.scut.acoustics.utils;

public class DSPMath {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * @param x   输入信号
     * @param n   FFT长度
     * @param fs  采样率
     * @param pxx 功率谱
     * @param f   频率
     */
    public native void welch(float[] x, int n, int fs, float[] pxx, float[] f);

    /**
     * @param x  输入信号
     * @param n  FFT长度
     * @param re 频域实部
     * @param im 频域虚部
     */
    public native void fft(float[] x, int n, float[] re, float[] im);

    /**
     * @param re 频域实部
     * @param im 频域虚部
     * @param n  FFT长度
     * @param x  输出信号
     */
    public native void ifft(float[] re, float[] im, int n, float[] x);

    /**
     * @param a 输入信号1
     * @param b 输入信号2
     * @param c 输出信号3
     */
    public native void conv(float[] a, float[] b, float[] c);

    /**
     * @param re  频域实部
     * @param im  频域虚部
     * @param rad 相位
     */
    public native void phase(float[] re, float[] im, float[] rad);

    /**
     * @param x  信号
     * @param l8 倍频程分压 63 125 250 500 1000 2000 4000 8000
     * @param ff 频率
     * @return 总分压
     */
    public native float aweight(short[] x, float[] l8, float[] ff);

    public native float zweight(short[] x, float[] l8, float[] ff);

    public native float cweight(short[] x, float[] l8, float[] ff);
}
