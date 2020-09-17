package infrastructure;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicLong;

final class CounterLog {
    private final RandomAccessFile[] raf;
    private final AtomicLong position;

    CounterLog(String dir) throws IOException {
        this.raf = new RandomAccessFile[3];

        this.raf[0] = new RandomAccessFile(String.format("%s/1.log", dir), "rw");
        this.raf[1] = new RandomAccessFile(String.format("%s/2.log", dir), "rw");
        this.raf[2] = new RandomAccessFile(String.format("%s/3.log", dir), "rw");

        this.position = new AtomicLong(this.raf[0].length());
    }

    public void write(long position, long counter) throws PersistenceException {
        try {
            this.raf[0].seek(position);
            this.raf[0].write(new byte[]{
                    (byte) ((counter >> 32) & 0xFF),
                    (byte) ((counter >> 24) & 0xFF),
                    (byte) ((counter >> 16) & 0xFF),
                    (byte) ((counter >> 8) & 0xFF),
                    (byte) ((counter >> 0) & 0xFF),
            });
        } catch (IOException exception) {
            throw new PersistenceException();
        }
    }

    public long nextPosition() {
        return this.position.addAndGet(5);
    }

    public long last() throws PersistenceException {
        long max = 0, curr, read = 0, toBeRead;
        byte[] buffer = new byte[5];

        try {
            toBeRead = this.raf[0].length();
            this.raf[0].seek(0);

            while (read < toBeRead) {
                this.raf[0].read(buffer, 0, 5);
                read += 5;
                curr = (buffer[0] & 0xFF) << 32 |
                        (buffer[1] & 0xFF) << 24 |
                        (buffer[2] & 0xFF) << 16 |
                        (buffer[3] & 0xFF) << 8 |
                        (buffer[4] & 0xFF) << 0;

                max = Math.max(max, curr);
            }
        } catch (IOException exception) {
            throw new PersistenceException();
        }

        return max;
    }
}
