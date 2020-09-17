package infrastructure;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Semaphore;

final class CounterLog {
    private final RandomAccessFile[] raf;
    private final Semaphore semaphore;
    private Integer logIndex = 0;
    private final int logQuantity;

    CounterLog(String dir, int logQuantity) throws IOException {
        this.raf = new RandomAccessFile[logQuantity];

        this.logQuantity = logQuantity;
        this.semaphore = new Semaphore(logQuantity);

        for (int i = 0; i < logQuantity; i++) {
            this.raf[i] = new RandomAccessFile(String.format("%s/%s.log", dir, i), "rw");
        }
    }

    public void write(long counter) throws PersistenceException {
        int logIndex;

        try {
            this.semaphore.acquire();

            synchronized (this.logIndex) {
                if (this.logQuantity-1 <= this.logIndex) { this.logIndex = 0; }
                else { this.logIndex++; }

                logIndex = this.logIndex;
            }

            this.raf[logIndex].write(new byte[]{
                    (byte) ((counter >> 32) & 0xFF),
                    (byte) ((counter >> 24) & 0xFF),
                    (byte) ((counter >> 16) & 0xFF),
                    (byte) ((counter >> 8) & 0xFF),
                    (byte) ((counter >> 0) & 0xFF),
            });

            this.semaphore.release();
        } catch (IOException | InterruptedException exception) {
            throw new PersistenceException();
        }
    }

    public long last() throws PersistenceException {
        long max = 0, curr, read = 0, toBeRead = 0;
        byte[] buffer = new byte[5];

        try {
            for (int i = 0; i < this.logQuantity; i++) {
                toBeRead = this.raf[i].length();
                this.raf[i].seek(0);

                while (read < toBeRead) {
                    this.raf[i].read(buffer, 0, 5);
                    read += 5;
                    curr = (buffer[0] & 0xFF) << 32 |
                            (buffer[1] & 0xFF) << 24 |
                            (buffer[2] & 0xFF) << 16 |
                            (buffer[3] & 0xFF) << 8 |
                            (buffer[4] & 0xFF) << 0;

                    max = Math.max(max, curr);
                }
            }
        } catch (IOException exception) {
            throw new PersistenceException();
        }

        return max;
    }
}
