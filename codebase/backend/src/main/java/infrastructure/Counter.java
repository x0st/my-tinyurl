package infrastructure;

import java.util.concurrent.atomic.AtomicLong;

final class Counter {
    private final CounterLog log;
    private final AtomicLong counter;
    private final int[] range;

    Counter(int[] range, CounterLog log) throws CounterException {
        try {
            this.log = log;
            this.range = range;
            this.counter = new AtomicLong();
            this.counter.set(log.last());

            if (this.counter.get() >= range[1]) {
                throw new CounterException("Exceeded the allowed range.");
            }

            if (this.counter.get() == 0) {
                this.counter.set(range[0]);
            }
        } catch (PersistenceException exception) {
            throw new CounterException(exception);
        }
    }

    public long increment() throws CounterException {
        final long counter = this.counter.incrementAndGet();

        if (counter >= this.range[1]) {
            throw new CounterException("Exceeded the allowed range.");
        }

        try {
            this.log.write(counter);
        } catch (PersistenceException exception) {
            throw new CounterException(exception);
        }

        return counter;
    }
}
