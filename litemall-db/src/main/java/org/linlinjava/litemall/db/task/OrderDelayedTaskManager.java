package org.linlinjava.litemall.db.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.service.LitemallGoodsProductService;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

@Component
public class OrderDelayedTaskManager {

    private static final Log log = LogFactory.getLog(OrderDelayedTaskManager.class);

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallGoodsProductService productService;

    private DelayQueue<OrderDelayedTask> queue;

    // 守护线程
    private Thread daemonThread;


    private static OrderDelayedTaskManager instance = new OrderDelayedTaskManager();

    private OrderDelayedTaskManager() {
        queue = new DelayQueue<>();
        init();
    }

    public static OrderDelayedTaskManager getInstance() {
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {
        daemonThread = new Thread(() -> execute());
        daemonThread.setName("DelayQueueMonitor");
        daemonThread.start();
    }


    public void execute() {
        while (true) {
            log.info("当前延时队列的任务数量:" + queue.size());
            try {
                OrderDelayedTask task = queue.take();
                if (null != task) {
                    LitemallOrder order = task.getOrder();
                    if (null == order) {
                        continue;
                    }
                    // 设置订单已取消状态
                    order.setOrderStatus(OrderUtil.STATUS_AUTO_CANCEL);
                    order.setEndTime(LocalDateTime.now());
                    if (orderService.updateWithOptimisticLocker(order) == 0) {
                        throw new RuntimeException("更新数据已失效");
                    }
                    // 商品货品数量增加
                    Integer orderId = order.getId();
                    List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
                    for (LitemallOrderGoods orderGoods : orderGoodsList) {
                        Integer productId = orderGoods.getProductId();
                        Short number = orderGoods.getNumber();
                        if (productService.addStock(productId, number) == 0) {
                            throw new RuntimeException("商品货品库存增加失败");
                        }
                    }
                    log.info("订单 ID" + order.getId() + " 已经超期自动取消订单");
                    log.info("end:" + LocalDateTime.now());
                }

            } catch (InterruptedException e) {
                log.error("获取延时任务失败！", e);
            }
        }
    }

    public void putQueue(LitemallOrder order) {
        OrderDelayedTask task = new OrderDelayedTask(order, TimeUnit.MINUTES.toMillis(30));
        queue.put(task);
    }

    public void removeQueue(OrderDelayedTask task) {
        queue.remove(task);
    }

}
