package edu.scut.acoustics.utils;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AudioRecorder {
    public static final int SOURCE = MediaRecorder.AudioSource.MIC;
    public static final int SAMPLE_RATE = 44100;
    public static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    public static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,CHANNEL,FORMAT) * 2;
    public static final int BIT_DEPTH = 16;
    public static final int BYTE_RATE = BIT_DEPTH * SAMPLE_RATE / 8;

    private static final byte header[] = new byte[44];

    //预先处理wav头
    static {
        header[0] = 'R';header[1] = 'I';header[2] = 'F';header[3] = 'F';

        //4-7文件长度，小端

        header[8] = 'W';header[9] = 'A';header[10] = 'V';header[11] = 'E';

        header[12] = 'f';header[13] = 'm';header[14] = 't';header[15] = ' ';

        header[16] = 16;header[17] = 0;header[18] = 0;header[19] = 0;

        //PCM格式
        header[20] = 1;header[21] = 0;

        //通道数
        header[22] = 1;header[23] = 0;

        //采样率
        header[24] = (byte) (SAMPLE_RATE & 0xff);
        header[25] = (byte) ((SAMPLE_RATE >> 8) & 0xff);
        header[26] = (byte) ((SAMPLE_RATE >> 16) & 0xff);
        header[27] = (byte) ((SAMPLE_RATE >> 24) & 0xff);
    }

    private boolean recording = false;
    private byte [] audioData;
    private AudioRecord recorder = null;
    private String filename;
    private ExecutorService service = Executors.newSingleThreadExecutor();
    private BufferedOutputStream bos = null;
    private Context context;
    private Future<Integer> future = null;

    public AudioRecorder(Context context) {
        this.context = context;
        //TODO 获得Handler
        filename = Objects.requireNonNull(context.getExternalCacheDir()).getAbsolutePath() + "/testAudio.wav";
    }

    public String getFilename() {
        return filename;
    }

    public void startRecording() throws FileNotFoundException {
        recorder = new AudioRecord(SOURCE,SAMPLE_RATE,CHANNEL,FORMAT,MIN_BUFFER_SIZE);
        recording = true;
        recorder.startRecording();
        bos = new BufferedOutputStream(new FileOutputStream(filename));
        Writer writer = new Writer();
        future = service.submit(writer);
    }

    /**
     * 阻塞方法，需要AsyncTask来执行
     * 只有执行完，才允许UI界面的开始按钮可用
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void stopRecording() throws ExecutionException, InterruptedException {
        recorder.stop();
        recording = false;
        recorder.release();
        recorder = null;
        future.get();
        future = null;
    }

    public void release(){
        if(recorder != null){
            recorder.release();
            recorder = null;
        }
    }

    class Writer implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            try {
                int length = 0;
                while ((length = recorder.read(audioData, 0, audioData.length)) != -1) {
                    bos.write(audioData, 0, length);
                }
                bos.close();
                return 0;
            }
            catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }
}
