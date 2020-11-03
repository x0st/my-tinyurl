package infrastructure.counter;

final public class CounterSnapshotThread extends Thread {
    private final Counter counter;
    private final CounterLog counterLog;
    private final long[] bounds;

    private long lastWrittenValue = 0;
    private Long synchronizedAt;

    public CounterSnapshotThread(Counter counter, CounterLog counterLog) {
        this.counter = counter;
        this.counterLog = counterLog;
        this.synchronizedAt = (long) 0;
        this.bounds = counter.bounds();
    }

    @Override
    public void run() {
        while (true) {
            long currentTimestamp = System.currentTimeMillis();
            if (currentTimestamp - synchronizedAt < 10000) {Thread.yield(); continue;}

            long newValue = this.counter.get();
            if (newValue == lastWrittenValue) {Thread.yield(); continue;}
            if (newValue >= this.bounds[1]) {Thread.yield(); continue;}

            this.counterLog.write(newValue);
            this.lastWrittenValue = newValue;
            this.synchronizedAt = currentTimestamp;
        }
    }
}
