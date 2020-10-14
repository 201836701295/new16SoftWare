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
     * @param a 音频数据
     * @return 分贝值
     */
    public native float mslm(float[] a);

    /**
     * @param re  频域实部
     * @param im  频域虚部
     * @param rad 相位
     */
    public native void phase(float[] re, float[] im, float[] rad);
}
