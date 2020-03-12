package org.linlinjava.litemall.core.task;

import com.google.common.primitives.Ints;

import java.time.LocalDateTime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class Task implements Delayed, Runnable{
    private String id = "";
    private long start = 0;

    public Task(String id, long delayInMilliseconds){
        this.id = id;
        this.start = System.currentTimeMillis() + delayInMilliseconds;
    }

    public String getId() {
        return id;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = this.start - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Ints.saturatedCast(this.start - ((Task) o).start);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Task)) {
            return false;
        }
        Task t = (Task)o;
        return this.id.equals(t.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
