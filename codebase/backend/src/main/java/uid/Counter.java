package uid;

import com.basho.riak.client.api.RiakClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicLong;

final public class Counter {
    private final Long rangeStart;
    private final Long rangeEnd;

    private final RiakClient riak;

    private final String counterKey;

    private RandomAccessFile raf;
    private final AtomicLong rafCounter = new AtomicLong(0);

    /**
     * Local copy of the counter.
     */
    private final AtomicLong counter = new AtomicLong(0);

    public Counter(Long rangeStart, Long rangeEnd, RiakClient riak) {
        if (rangeEnd <= rangeStart)
            throw new RuntimeException("The end must be greater than the start.");

        this.counterKey = String.format("range:%s-%s", rangeStart, rangeEnd);

        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;

        this.riak = riak;

        try {
            this.raf = new RandomAccessFile("/var/lib/tinyurl/counter.log", "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public long next() {
        if (this.counter.get() >= this.rangeEnd) throw new RuntimeException("Reached the end.");

        return readFromLocal();
    }

    private long readFromLocal() {
        long counter = this.counter.incrementAndGet();

        try {
            this.raf.seek(this.rafCounter.getAndIncrement() * 40);
            this.raf.write(new byte[] {
                    (byte) ((counter >> 32) & 0xFF),
                    (byte) ((counter >> 24) & 0xFF),
                    (byte) ((counter >> 16) & 0xFF),
                    (byte) ((counter >>  8) & 0xFF),
                    (byte) ((counter >>  0) & 0xFF),
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return counter;
    }
}
