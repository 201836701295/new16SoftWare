package edu.scut.acoustics.utils;

/**
 * 生产正弦波
 */
public class SinWave {
    public final static double TWO_PI = 2 * Math.PI;
    //采样率
    public final static int SAMPLE_RATE = 44100;

    //正弦波振幅高度
    private double height;
    private int hz;

    /**
     * @param hz 频率
     * @param db 分贝
     */
    public SinWave(int hz, int db) {
        this.hz = hz;
        double temp = (double) db / 10;
        temp = Math.pow(10, temp) * 2;
        height = Math.sqrt(temp);
    }

    public SinWave() {
        hz = -1;
        height = -1.0;
    }

    public void generate(float[] wave, int offset, int step) throws Exception {
        if (hz == -1f || height == -1.0) {
            throw new Exception("hz and height not initialized");
        }
        //求波长
        double wave_length = (1.0 * SAMPLE_RATE) / hz;
        int temp;
        for (int i = offset; i < wave.length; i += step) {
            temp = (i - offset) / step;
            wave[i] = (float) (height * Math.sin(TWO_PI * (temp % wave_length) / wave_length));
        }
    }

    /**
     * @param wave 输出的正弦波
     */
    public void doFinal(float[] wave) {
        //求波长
        double wave_length = (1.0 * SAMPLE_RATE) / hz;

        for (int i = 0; i < wave.length; i++) {
            wave[i] = (float) (height * Math.sin(TWO_PI * (i % wave_length) / wave_length));
        }
    }

    /**
     * 设置频率和音量
     *
     * @param hz 频率
     * @param db 分贝
     */
    public void set(int hz, int db) {
        this.hz = hz;
        double temp = (double) db / 10;
        temp = Math.pow(10, temp) * 2;
        height = Math.sqrt(temp) / 32768;
    }
}
