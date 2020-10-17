package infrastructure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicLong;

final class Counter {
    private final RandomAccessFile raf;
    private final AtomicLong counter;
    private final int[] range;

    Counter(int[] range, String log) throws ExceededRangeException {
        try {
            this.range = range;
            this.counter = new AtomicLong();
            this.raf = new RandomAccessFile(log, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (this.counter.get() >= range[1]) {
            throw new ExceededRangeException();
        }

        if (this.counter.get() == 0) {
            this.counter.set(range[0]);
        }

        this.recover();
    }

    public long increment() throws ExceededRangeException {
        final long counter = this.counter.incrementAndGet();

        if (counter >= this.range[1]) {
            throw new ExceededRangeException();
        }

        try {
            this.raf.write(new byte[]{
                    (byte) ((counter >> 32) & 0xFF),
                    (byte) ((counter >> 24) & 0xFF),
                    (byte) ((counter >> 16) & 0xFF),
                    (byte) ((counter >> 8) & 0xFF),
                    (byte) ((counter >> 0) & 0xFF),
            });
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return counter;
    }

    private void recover() {
        long max = 0, curr, read = 0, toBeRead = 0;
        byte[] buffer = new byte[5];

        try {
            toBeRead = this.raf.length();
            this.raf.seek(0);

            while (read < toBeRead) {
                this.raf.read(buffer, 0, 5);
                read += 5;
                curr = (buffer[0] & 0xFF) << 32 |
                        (buffer[1] & 0xFF) << 24 |
                        (buffer[2] & 0xFF) << 16 |
                        (buffer[3] & 0xFF) << 8 |
                        (buffer[4] & 0xFF) << 0;

                max = Math.max(max, curr);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.counter.set(max);
    }
}
