package org.linlinjava.litemall.db.task;

import org.linlinjava.litemall.db.domain.LitemallOrder;

import java.time.ZoneOffset;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class OrderDelayedTask implements Delayed {


    private LitemallOrder order;
    /**
     * 延时时间 单位 timeunit.milliseconds
     **/
    private long timeout;
    private long exprire;

    public OrderDelayedTask(LitemallOrder order, long timeout) {
        this.order = order;
        this.timeout = timeout;
        this.exprire = order.getAddTime().toInstant(ZoneOffset.of("+8")).toEpochMilli() + timeout;
    }

    public LitemallOrder getOrder() {
        return order;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(exprire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        OrderDelayedTask other = (OrderDelayedTask) o;
        long diff = order.getAddTime().toInstant(ZoneOffset.of("+8")).toEpochMilli()
                - other.getOrder().getAddTime().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "OrderDelayedTask{" +
                "order=" + order +
                ", timeout=" + timeout +
                '}';
    }
}
