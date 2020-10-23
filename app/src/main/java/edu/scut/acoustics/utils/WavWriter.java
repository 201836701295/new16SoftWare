package edu.scut.acoustics.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WavWriter {
    public static final int SAMPLE_RATE = 44100;
    public static final int BIT_DEPTH = 16;
    public static final int BYTE_RATE = BIT_DEPTH * SAMPLE_RATE / 8;

    byte[] header = new byte[44];
    long audioLength = 0;

    File file;
    BufferedOutputStream bos;
    RandomAccessFile raf;


    public WavWriter(File f) {
        file = f;
        initialHeader();
    }

    public void open() throws IOException {
        if (bos == null) {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(header);
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        bos.write(b, off, len);
        audioLength += (len / 2);
    }

    public void close() throws IOException {
        if (bos != null) {
            bos.close();
            bos = null;

            raf = new RandomAccessFile(file, "rw");
            writeLength();
            raf.close();
            raf = null;
        }
    }

    void writeLength() throws IOException {
        long dataLength = audioLength + 36;
        //寻道到下标为4位置，写入数据长度
        raf.seek(4);
        raf.write((int) (dataLength & 0xff));
        raf.write((int) (dataLength >> 8 & 0xff));
        raf.write((int) (dataLength >> 16 & 0xff));
        raf.write((int) (dataLength >> 24 & 0xff));
        //寻道到下标为40位置，写入音频长度
        raf.seek(40);
        raf.write((int) (audioLength & 0xff));
        raf.write((int) (audioLength >> 8 & 0xff));
        raf.write((int) (audioLength >> 16 & 0xff));
        raf.write((int) (audioLength >> 24 & 0xff));
    }

    void initialHeader() {
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
}
