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
        int bytesReadNow = super.read(b, off, len);
        if (bytesReadNow > 0) {
            bytesRead += bytesReadNow;

            long elapsed = System.currentTimeMillis() - startTime;
            double expectedBytes = maxBytesPerSecond * (elapsed / 1000.0);

            if (bytesRead > expectedBytes) {
                long sleepTime = (long) (((bytesRead - expectedBytes) / maxBytesPerSecond) * 1000);
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Thread interrupted during throttling", e);
                    }
                }
            }
        }
        return bytesReadNow;
    }

}