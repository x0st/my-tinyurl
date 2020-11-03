package infrastructure.counter;

import java.util.concurrent.atomic.AtomicLong;

final public class Counter {
    private final AtomicLong counter;
    private final long[] range;

    public Counter(long[] range, long currentValue) throws CounterException {
        this.range = range;
        this.counter = new AtomicLong();
        this.counter.set(currentValue);

        if (this.counter.get() >= range[1]) {
            throw new CounterException("Exceeded the allowed range.");
        }

        if (currentValue == 0) {
            this.counter.set(range[0]);
        }
    }

    public long increment() throws CounterException {
        long newValue = this.counter.incrementAndGet();

        if (newValue >= this.range[1]) {
            throw new CounterException("Exceeded the allowed range.");
        }

        return newValue;
    }

    long[] bounds() {
        return this.range;
    }

    long get() {
        return this.counter.get();
    }
}
