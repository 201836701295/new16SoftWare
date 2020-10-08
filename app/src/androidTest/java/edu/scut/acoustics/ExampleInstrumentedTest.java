package edu.scut.acoustics;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import edu.scut.acoustics.utils.DSPMath;
import edu.scut.acoustics.utils.SinWave;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws IOException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("edu.scut.acoustics", appContext.getPackageName());

        MediaExtractor mediaExtractor = new MediaExtractor();
        AssetFileDescriptor assetFileDescriptor = appContext.getResources().openRawResourceFd(R.raw.sample_signal);
        mediaExtractor.setDataSource(assetFileDescriptor);
        Log.d("TrackCount ", String.valueOf(mediaExtractor.getTrackCount()));
        MediaCodec mediaDecode = null;
        for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
            MediaFormat format = mediaExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            System.out.println(mime);
            assert mime != null;
            Log.d("KEY_MIME ", mime);
            if (mime.startsWith("audio")) {//获取音频轨道
//                    format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 200 * 1024);
                mediaExtractor.selectTrack(i);//选择此音频轨道
                mediaDecode = MediaCodec.createDecoderByType(mime);//创建Decode解码器
                mediaDecode.configure(format, null, null, 0);
                break;
            }
        }
        assert mediaDecode != null;
        mediaDecode.start();

    }

    @Test
    public void test() {
        float[] a = new float[8192];
        SinWave sinWave = new SinWave(2000, 40);
        sinWave.doFinal(a);
        for (int i = 0; i < a.length; i++) {
            a[i] *= ((int) Short.MAX_VALUE + 1);
        }
        DSPMath dspMath = new DSPMath();
        System.out.println(dspMath.mslm(a));
    }

    @Test
    public void testfft() throws Exception {
        float[] a = new float[441];
        float[] re = new float[a.length];
        float[] im = new float[a.length];
        SinWave sinWave = new SinWave();
        sinWave.set(200, 100);
        sinWave.generate(a, 0, 1);
        DSPMath dspMath = new DSPMath();
        dspMath.fft(a, a.length, re, im);
        for (int i = 0; i < re.length; i++) {
            System.out.println(re[i] + " " + im[i] + "j");
        }
    }
}