package net.ddns.levelcloud.music.util;

import org.springframework.beans.factory.annotation.Value;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ThrottledInputStream extends FilterInputStream {
    private long bytesRead;
    private long startTime;

    @Value("${upload.throttle.speed:1.5}")
    private double maxBytesPerSecond;

    public ThrottledInputStream(InputStream in) {
        super(in);
        this.bytesRead = 0;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 0 && bytesRead >= maxBytesPerSecond * elapsed / 1000) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Thread interrupted during throttling", e);
            }
        }
        int bytesReadNow = super.read(b, off, len);
        if (bytesReadNow > 0) {
            bytesRead += bytesReadNow;
        }
        return bytesReadNow;
    }
}