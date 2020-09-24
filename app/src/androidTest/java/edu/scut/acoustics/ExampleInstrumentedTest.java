package edu.scut.acoustics;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileDescriptor;
import java.io.IOException;

import edu.scut.acoustics.utils.SinWave;

import static org.junit.Assert.*;

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
    public void test(){
        int length = AudioTrack.getMinBufferSize(SinWave.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        Log.d("TAG", "test: " + length);
    }
}