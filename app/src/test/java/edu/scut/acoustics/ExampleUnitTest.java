package edu.scut.acoustics;

import org.junit.Test;

import edu.scut.acoustics.utils.SinWave;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        System.out.println((float) Short.MAX_VALUE / ((int) Short.MAX_VALUE + 1));
    }

    @Test
    public void audio(){
        int length = SinWave.SAMPLE_RATE * 100;
        System.out.println(length);
        float[] data = new float[length];
        int[] freq_list = {250,500,1000,2000,4000,8000};
        int[] db_list = {0,5,10,15,20,25,30,35,40,45,50,55,60,65,70};
        SinWave sinWave = new SinWave(25,40);

        for(int f : freq_list){
            System.out.println(f + "hz: ");
            for(int d : db_list){
                sinWave.set(f,d);
                sinWave.doFinal(data);
                double sum = 0;
                for (float v: data) {
                    sum += (double) v * v;
                }
                sum /= data.length;
                System.out.println("    " + d + "db: " + 10.0 * Math.log10(sum));
            }

        }

    }
}