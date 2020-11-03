package infrastructure.counter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

final public class CounterLog {
    private final MappedByteBuffer mappedByteBuffer;

    public CounterLog(String dir) throws IOException {
        this.mappedByteBuffer =
                new RandomAccessFile(String.format("%s/counter.log", dir), "rw")
                .getChannel()
                .map(FileChannel.MapMode.READ_WRITE, 0, 8);
    }

    synchronized void write(long counter) {
        this.mappedByteBuffer.position(0);
        this.mappedByteBuffer.putLong(counter);
        this.mappedByteBuffer.force();
    }

    public long restore() {
        return this.mappedByteBuffer.getLong(0);
    }
}
