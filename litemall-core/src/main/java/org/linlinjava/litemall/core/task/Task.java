package org.linlinjava.litemall.core.task;

import com.google.common.primitives.Ints;

import java.time.LocalDateTime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延迟任务抽象基类。
 *
 * <p>实现 {@link Delayed} 与 {@link Runnable}，子类在 {@link #run()} 中定义到期要执行的具体逻辑；
 * 任务以 id 标识，支持在延迟队列中的排序、相等判断与移除。
 */
public abstract class Task implements Delayed, Runnable{
    /** 任务唯一标识 */
    private String id = "";
    /** 任务应执行的绝对时间戳（毫秒） */
    private long start = 0;

    /**
     * 构造一个延迟任务
     *
     * @param id                   任务唯一标识
     * @param delayInMilliseconds  延迟执行的时间（毫秒）
     */
    public Task(String id, long delayInMilliseconds){
        this.id = id;
        this.start = System.currentTimeMillis() + delayInMilliseconds;
    }

    /**
     * 获取任务标识
     *
     * @return 任务唯一标识
     */
    public String getId() {
        return id;
    }

    /**
     * 距任务执行的剩余延迟时间
     *
     * @param unit 时间单位
     * @return 剩余延迟时间，按指定单位换算，可为负
     */
    @Override
    public long getDelay(TimeUnit unit) {
        long diff = this.start - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    /**
     * 按计划执行时间比较任务先后
     *
     * @param o 待比较的延迟任务
     * @return 负数/零/正数，表示本任务早于/同时/晚于比较对象
     */
    @Override
    public int compareTo(Delayed o) {
        return Ints.saturatedCast(this.start - ((Task) o).start);
    }

    /**
     * 任务相等性按 id 判断
     */
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

    /**
     * 哈希值基于任务 id
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
