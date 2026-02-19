package io.gitub.hyscript7.drony.workers;

public class CounterImpl implements Counter {
    private int count;

    public CounterImpl() {
        this.count = 0;
    }

    @Override
    public synchronized int getCount() {
        return count;
    }

    @Override
    public synchronized void increment() {
        count++;
    }
}
