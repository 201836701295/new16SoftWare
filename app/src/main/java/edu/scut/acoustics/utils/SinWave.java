package edu.scut.acoustics.utils;

public class SinWave {
    public final static double TWO_PI = 2 * Math.PI;
    //采样率
    public final static int SAMPLE_RATE = 44100;

    //正弦波振幅高度
    private double height;
    private int hz;

    public SinWave(int hz, int db){
        this.hz = hz;
        double temp = (double) db / 10;
        temp = Math.pow(10,temp) * 2;
        height = Math.sqrt(temp);
    }

    /**
     *
     * @param wave 输出的正弦波
     */
    public void doFinal(float[] wave){
        double wave_length = ( 1.0 * SAMPLE_RATE ) / hz;
        for (int i = 0; i < wave.length; i++) {
            wave[i] = (float) (height * Math.sin(TWO_PI * (i % wave_length) / wave_length));
        }
    }

    /**
     *
     * @param hz 频率
     * @param db 分贝
     */
    public void set(int hz, int db){
        this.hz = hz;
        double temp = (double) db / 10;
        temp = Math.pow(10,temp) * 2;
        height = Math.sqrt(temp) / 32768;
    }
}
