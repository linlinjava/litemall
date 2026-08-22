package org.linlinjava.litemall.wx.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.service.LitemallCouponUserService;
import org.linlinjava.litemall.db.service.LitemallGoodsProductService;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.util.CouponUserConstant;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Owns the transaction boundary for unpaid-order timeout compensation.
 *
 * <p>The scheduler task is created with {@code new}, so it cannot provide a
 * Spring transaction proxy itself. This Spring-managed service is the single
 * transaction boundary for the order state and all resource compensations.</p>
 */
@Service
public class OrderTimeoutCompensationService {
    private final Log logger = LogFactory.getLog(OrderTimeoutCompensationService.class);

    private final LitemallOrderService orderService;
    private final LitemallOrderGoodsService orderGoodsService;
    private final LitemallGoodsProductService productService;
    private final LitemallCouponUserService couponUserService;

    public OrderTimeoutCompensationService(LitemallOrderService orderService,
                                           LitemallOrderGoodsService orderGoodsService,
                                           LitemallGoodsProductService productService,
                                           LitemallCouponUserService couponUserService) {
        this.orderService = orderService;
        this.orderGoodsService = orderGoodsService;
        this.productService = productService;
        this.couponUserService = couponUserService;
    }

    /**
     * Closes one unpaid order and compensates all resources atomically.
     *
     * <p>A non-unpaid order is an idempotent no-op. If the optimistic-lock
     * update loses a race, another task owns the order and this invocation is
     * also a safe no-op. Any resource failure is thrown so Spring rolls back
     * the order transition and all prior compensation updates.</p>
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeOrder(Integer orderId) {
        LitemallOrder order = orderService.findById(orderId);
        if (order == null || !OrderUtil.isCreateStatus(order)) {
            logger.info("超时订单任务跳过，订单号=" + orderId);
            return;
        }

        order.setOrderStatus(OrderUtil.STATUS_AUTO_CANCEL);
        order.setEndTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            logger.info("超时订单任务竞争失败，订单号=" + orderId);
            return;
        }

        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
        for (LitemallOrderGoods orderGoods : orderGoodsList) {
            if (productService.addStock(orderGoods.getProductId(), orderGoods.getNumber()) == 0) {
                throw new RuntimeException("商品货品库存增加失败");
            }
        }

        List<LitemallCouponUser> couponUsers = couponUserService.findByOid(orderId);
        for (LitemallCouponUser couponUser : couponUsers) {
            couponUser.setStatus(CouponUserConstant.STATUS_USABLE);
            couponUser.setUpdateTime(LocalDateTime.now());
            if (couponUserService.update(couponUser) == 0) {
                throw new RuntimeException("优惠券返还失败");
            }
        }

        logger.info("超时订单任务处理成功，订单号=" + orderId);
    }
}
