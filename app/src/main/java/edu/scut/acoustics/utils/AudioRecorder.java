package edu.scut.acoustics.utils;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
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
    public static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, FORMAT) * 8;
    public static final int BIT_DEPTH = 16;
    public static final int BYTE_RATE = BIT_DEPTH * SAMPLE_RATE / 8;

    private static final byte[] header = new byte[44];

    //预先处理wav头
    static {
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';

        //4-7文件长度，小端
        //Size是整个文件的长度减去ID和Size的长度
        header[4] = 0;
        header[5] = 0;
        header[6] = 0;
        header[7] = 0;

        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';

        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;

        //PCM格式
        header[20] = 1;
        header[21] = 0;

        //通道数
        header[22] = 1;
        header[23] = 0;

        //采样率
        header[24] = (byte) (SAMPLE_RATE & 0xff);
        header[25] = (byte) ((SAMPLE_RATE >> 8) & 0xff);
        header[26] = (byte) ((SAMPLE_RATE >> 16) & 0xff);
        header[27] = (byte) ((SAMPLE_RATE >> 24) & 0xff);

        header[28] = (byte) (BYTE_RATE & 0xff);
        header[29] = (byte) ((BYTE_RATE >> 8) & 0xff);
        header[30] = (byte) ((BYTE_RATE >> 16) & 0xff);
        header[31] = (byte) ((BYTE_RATE >> 24) & 0xff);

        header[32] = (byte) (16 / 8);
        header[33] = 0;

        header[34] = 16;
        header[35] = 0;

        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';

        //音频长度
        header[40] = 0;
        header[41] = 0;
        header[42] = 0;
        header[43] = 0;
    }

    private boolean recording = false;
    private byte[] audioData = new byte[MIN_BUFFER_SIZE];
    private AudioRecord recorder = null;
    private String filename;
    private ExecutorService service = Executors.newSingleThreadExecutor();
    private BufferedOutputStream bos = null;
    private Future<Integer> future = null;

    public AudioRecorder(Context context) {
        filename = Objects.requireNonNull(context.getExternalCacheDir()).getAbsolutePath() + "/testAudio.wav";
    }

    public boolean isRecording() {
        return recording;
    }

    public String getFilename() {
        return filename;
    }

    public void start() throws IOException {
        //创建录音机
        recorder = new AudioRecord(SOURCE, SAMPLE_RATE, CHANNEL, FORMAT, MIN_BUFFER_SIZE);
        recording = true;
        //创建文件输出流
        bos = new BufferedOutputStream(new FileOutputStream(filename));
        bos.write(header);
        //开始录音
        recorder.startRecording();
        //创建写入线程
        Writer writer = new Writer();
        future = service.submit(writer);
    }

    /**
     * 阻塞方法，需要AsyncTask来执行
     * 只有执行完，才允许UI界面的开始按钮可用
     */
    public void stop() throws ExecutionException, InterruptedException {
        //停止录音
        if (recorder != null) {
            recorder.stop();
            recording = false;
            //等待写入线程结束
            if (future != null) {
                future.get();
                future = null;
            }
            //释放录音机
            recorder.release();
            recorder = null;
        }
    }

    class Writer implements Callable<Integer> {
        RandomAccessFile raf = null;
        //音频长度
        long audio_length = 0;

        @Override
        public Integer call() throws Exception {
            try {
                //先写入wav文件头
                bos.write(header, 0, header.length);
                int length = 0;
                //写入音频数据到文件
                while ((length = recorder.read(audioData, 0, audioData.length)) != 0) {
                    bos.flush();
                    audio_length += length;
                    bos.write(audioData, 0, length);
                }
                //关闭文件流
                bos.flush();
                bos.close();
                //开启随机访问文件
                raf = new RandomAccessFile(filename, "rw");
                //写入文件长度、音频长度的信息
                return write_length();
            } catch (IOException e) {
                e.printStackTrace();
                //如果出错返回-1
                return -1;
            }
        }

        public int write_length() throws IOException {
            //数据长度为
            long data_length = audio_length + 36;
            if (raf == null) {
                return -1;
            }
            //寻道到下标为4位置，写入数据长度
            raf.seek(4);
            raf.write((int) (data_length & 0xff));
            raf.write((int) (data_length >> 8 & 0xff));
            raf.write((int) (data_length >> 16 & 0xff));
            raf.write((int) (data_length >> 24 & 0xff));
            //寻道到下标为40位置，写入音频长度
            raf.seek(40);
            raf.write((int) (audio_length & 0xff));
            raf.write((int) (audio_length >> 8 & 0xff));
            raf.write((int) (audio_length >> 16 & 0xff));
            raf.write((int) (audio_length >> 24 & 0xff));
            //关闭随机文件访问
            raf.close();
            raf = null;
            Log.d("recorder is finish", getFilename());
            return 0;
        }
    }
}
