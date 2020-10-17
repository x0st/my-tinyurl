package infrastructure;

import java.io.IOException;
import java.io.RandomAccessFile;

import domain.Key;
import domain.KeyRepository;

final public class KeyRepositoryFileImpl implements KeyRepository {
    private final RandomAccessFile raf;

    public KeyRepositoryFileImpl(String file) throws IOException {
        this.raf = new RandomAccessFile(file, "rw");
        this.raf.seek(this.raf.length());
    }

    @Override
    public synchronized void persist(Key key) {
        try {
            this.raf.write(key.toBytes());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
