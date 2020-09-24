package edu.scut.acoustics.utils;

import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioDecoder {
    private BufferedInputStream bis;
    /*
    MediaExtractor mediaExtractor = new MediaExtractor();

    public void setDataSource(AssetFileDescriptor assetFileDescriptor) throws IOException {
        mediaExtractor.setDataSource(assetFileDescriptor);
    }
     */

    /**
     * 16位简陋wav解码器
     *
     * @param fd
     * @throws IOException
     */
    public AudioDecoder(FileDescriptor fd) throws IOException {
        bis = new BufferedInputStream(new FileInputStream(fd));
        for (int i = 0; i < 44; i++) {
            bis.read();
        }
    }

    public int read(short[] data, int off, int length) throws IOException {
        int temp1, temp2;
        for (int i = 0; i < length; i++) {
            temp1 = bis.read();
            if (temp1 < 0) {
                return i;
            }
            temp2 = bis.read();
            if (temp2 < 0) {
                return i;
            }
            data[i] = (short) (temp1 | (temp2 << 8));
        }
        return length;
    }
}
