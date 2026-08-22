package org.linlinjava.litemall.wx.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.task.Task;
import org.linlinjava.litemall.core.util.BeanUtil;
import org.linlinjava.litemall.wx.service.OrderTimeoutCompensationService;

public class OrderUnpaidTask extends Task {
    private final Log logger = LogFactory.getLog(OrderUnpaidTask.class);
    private int orderId = -1;

    public OrderUnpaidTask(Integer orderId, long delayInMilliseconds){
        super("OrderUnpaidTask-" + orderId, delayInMilliseconds);
        this.orderId = orderId;
    }

    public OrderUnpaidTask(Integer orderId){
        super("OrderUnpaidTask-" + orderId, SystemConfig.getOrderUnpaid() * 60 * 1000);
        this.orderId = orderId;
    }

    @Override
    public void run() {
        logger.info("系统开始处理延时任务---订单超时未付款---" + this.orderId);
        try {
            getCompensationService().closeOrder(this.orderId);
        } catch (RuntimeException exception) {
            logger.error("系统处理延时任务失败---订单超时未付款---订单号=" + this.orderId
                    + ", 原因=" + exception.getMessage(), exception);
            throw exception;
        }
        logger.info("系统结束处理延时任务---订单超时未付款---" + this.orderId);
    }

    /**
     * Resolve the Spring-managed service at execution time because this task is
     * deliberately created with {@code new} by the task scheduler.
     */
    protected OrderTimeoutCompensationService getCompensationService() {
        return BeanUtil.getBean(OrderTimeoutCompensationService.class);
    }
}
