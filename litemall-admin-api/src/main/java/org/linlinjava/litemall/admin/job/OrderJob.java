package org.linlinjava.litemall.admin.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.domain.LitemallGoodsProduct;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 检测订单状态
 */
@Component
public class OrderJob {
    private final Log logger = LogFactory.getLog(OrderJob.class);

    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallGoodsProductService productService;

    /**
     * 自动取消订单
     * <p>
     * 定时检查订单未付款情况，如果超时半个小时则自动取消订单
     * 定时时间是每次相隔半个小时。
     * <p>
     * 注意，因为是相隔半小时检查，因此导致有订单是超时一个小时以后才设置取消状态。
     * TODO
     * 这里可以进一步地配合用户订单查询时订单未付款检查，如果订单超时半小时则取消。
     */
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void checkOrderUnpaid() {
        logger.info("系统开启任务检查订单是否已经超期自动取消订单");

        List<LitemallOrder> orderList = orderService.queryUnpaid();
        for (LitemallOrder order : orderList) {
            LocalDateTime add = order.getAddTime();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expired = add.plusMinutes(30);
            if (expired.isAfter(now)) {
                continue;
            }

            // 开启事务管理
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus status = txManager.getTransaction(def);
            try {
                // 设置订单已取消状态
                order.setOrderStatus(OrderUtil.STATUS_AUTO_CANCEL);
                order.setEndTime(LocalDateTime.now());
                if (orderService.updateWithOptimisticLocker(order) == 0) {
                    throw new Exception("更新数据已失效");
                }

                // 商品货品数量增加
                Integer orderId = order.getId();
                List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
                for (LitemallOrderGoods orderGoods : orderGoodsList) {
                    Integer productId = orderGoods.getProductId();
                    LitemallGoodsProduct product = productService.findById(productId);
                    Short number = orderGoods.getNumber();
                    if (productService.addStock(productId, number) == 0) {
                        throw new Exception("商品货品库存增加失败");
                    }
                }
            } catch (Exception ex) {
                txManager.rollback(status);
                logger.info("订单 ID=" + order.getId() + " 数据更新失败，放弃自动确认收货");
                return;
            }
            txManager.commit(status);
            logger.info("订单 ID=" + order.getId() + " 已经超期自动取消订单");
        }
    }

    /**
     * 自动确认订单
     * <p>
     * 定时检查订单未确认情况，如果超时七天则自动确认订单
     * 定时时间是每天凌晨3点。
     * <p>
     * 注意，因为是相隔一天检查，因此导致有订单是超时八天以后才设置自动确认。
     * 这里可以进一步地配合用户订单查询时订单未确认检查，如果订单超时7天则自动确认。
     * 但是，这里可能不是非常必要。相比订单未付款检查中存在商品资源有限所以应该
     * 早点清理未付款情况，这里八天再确认是可以的。。
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void checkOrderUnconfirm() {
        logger.info("系统开启任务检查订单是否已经超期自动确认收货");

        List<LitemallOrder> orderList = orderService.queryUnconfirm();
        for (LitemallOrder order : orderList) {
            LocalDateTime ship = order.getShipTime();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expired = ship.plusDays(7);
            if (expired.isAfter(now)) {
                continue;
            }

            // 设置订单已取消状态
            order.setOrderStatus(OrderUtil.STATUS_AUTO_CONFIRM);
            order.setConfirmTime(now);
            if (orderService.updateWithOptimisticLocker(order) == 0) {
                logger.info("订单 ID=" + order.getId() + " 数据已经更新，放弃自动确认收货");
            } else {
                logger.info("订单 ID=" + order.getId() + " 已经超期自动确认收货");
            }
        }
    }

    /**
     * 可评价订单商品超期
     * <p>
     * 定时检查订单商品评价情况，如果确认商品超时七天则取消可评价状态
     * 定时时间是每天凌晨4点。
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void checkOrderComment() {
        logger.info("系统开启任务检查订单是否已经超期未评价");

        LocalDateTime now = LocalDateTime.now();
        List<LitemallOrder> orderList = orderService.queryComment();
        for (LitemallOrder order : orderList) {
            LocalDateTime confirm = order.getConfirmTime();
            LocalDateTime expired = confirm.plusDays(7);
            if (expired.isAfter(now)) {
                continue;
            }

            order.setComments((short) 0);
            orderService.updateWithOptimisticLocker(order);

            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                orderGoods.setComment(-1);
                orderGoodsService.updateById(orderGoods);
            }
        }
    }
}
