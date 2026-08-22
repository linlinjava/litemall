package org.linlinjava.litemall.tests.unit;

import org.junit.Before;
import org.junit.Test;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.service.LitemallCouponUserService;
import org.linlinjava.litemall.db.service.LitemallGoodsProductService;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.util.CouponUserConstant;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.service.OrderTimeoutCompensationService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class OrderTimeoutCompensationServiceUnitTest {
    private LitemallOrderService orderService;
    private LitemallOrderGoodsService orderGoodsService;
    private LitemallGoodsProductService productService;
    private LitemallCouponUserService couponUserService;
    private OrderTimeoutCompensationService service;

    @Before
    public void setUp() {
        orderService = mock(LitemallOrderService.class);
        orderGoodsService = mock(LitemallOrderGoodsService.class);
        productService = mock(LitemallGoodsProductService.class);
        couponUserService = mock(LitemallCouponUserService.class);
        service = new OrderTimeoutCompensationService(orderService, orderGoodsService, productService, couponUserService);
    }

    @Test
    public void mallOrdTimeoutAc01BeUt001_closesOrderAndReleasesResources() {
        LitemallOrder order = order(1001, OrderUtil.STATUS_CREATE);
        LitemallOrderGoods goods = orderGoods(1, 2);
        LitemallCouponUser coupon = coupon(CouponUserConstant.STATUS_USED);
        when(orderService.findById(1001)).thenReturn(order);
        when(orderService.updateWithOptimisticLocker(order)).thenReturn(1);
        when(orderGoodsService.queryByOid(1001)).thenReturn(Collections.singletonList(goods));
        when(productService.addStock(1, (short) 2)).thenReturn(1);
        when(couponUserService.findByOid(1001)).thenReturn(Collections.singletonList(coupon));
        when(couponUserService.update(coupon)).thenReturn(1);

        service.closeOrder(1001);

        assertEquals(OrderUtil.STATUS_AUTO_CANCEL, order.getOrderStatus());
        assertNotNull(order.getEndTime());
        assertEquals(CouponUserConstant.STATUS_USABLE, coupon.getStatus());
        verify(productService).addStock(1, (short) 2);
        verify(couponUserService).update(coupon);
    }

    @Test
    public void mallOrdTimeoutAc02BeUt001_terminalOrderIsSkippedWithoutSideEffects() {
        LitemallOrder order = order(1001, OrderUtil.STATUS_AUTO_CANCEL);
        when(orderService.findById(1001)).thenReturn(order);

        service.closeOrder(1001);

        verify(orderService, never()).updateWithOptimisticLocker(any(LitemallOrder.class));
        verifyNoInteractions(orderGoodsService, productService, couponUserService);
    }

    @Test
    public void mallOrdTimeoutAc03BeUt001_stockFailureIsPropagatedBeforeCouponRelease() {
        LitemallOrder order = order(1001, OrderUtil.STATUS_CREATE);
        LitemallOrderGoods goods = orderGoods(1, 2);
        when(orderService.findById(1001)).thenReturn(order);
        when(orderService.updateWithOptimisticLocker(order)).thenReturn(1);
        when(orderGoodsService.queryByOid(1001)).thenReturn(Collections.singletonList(goods));
        when(productService.addStock(1, (short) 2)).thenReturn(0);

        try {
            service.closeOrder(1001);
            fail("stock failure must fail the transaction boundary");
        } catch (RuntimeException expected) {
            assertEquals("商品货品库存增加失败", expected.getMessage());
        }

        verifyNoInteractions(couponUserService);
    }

    @Test
    public void mallOrdTimeoutAc06BeUt001_optimisticLockLossIsSafeSkip() {
        LitemallOrder order = order(1001, OrderUtil.STATUS_CREATE);
        when(orderService.findById(1001)).thenReturn(order);
        when(orderService.updateWithOptimisticLocker(order)).thenReturn(0);

        service.closeOrder(1001);

        verifyNoInteractions(orderGoodsService, productService, couponUserService);
    }

    @Test
    public void mallOrdTimeoutAc04BeUt001_couponFailureIsPropagated() {
        LitemallOrder order = order(1001, OrderUtil.STATUS_CREATE);
        LitemallCouponUser coupon = coupon(CouponUserConstant.STATUS_USED);
        when(orderService.findById(1001)).thenReturn(order);
        when(orderService.updateWithOptimisticLocker(order)).thenReturn(1);
        when(orderGoodsService.queryByOid(1001)).thenReturn(Collections.<LitemallOrderGoods>emptyList());
        when(couponUserService.findByOid(1001)).thenReturn(Collections.singletonList(coupon));
        when(couponUserService.update(coupon)).thenReturn(0);

        try {
            service.closeOrder(1001);
            fail("coupon failure must fail the transaction boundary");
        } catch (RuntimeException expected) {
            assertEquals("优惠券返还失败", expected.getMessage());
        }
    }

    private LitemallOrder order(Integer id, Short status) {
        LitemallOrder order = new LitemallOrder();
        order.setId(id);
        order.setOrderStatus(status);
        order.setUpdateTime(LocalDateTime.of(2026, 7, 11, 10, 0));
        return order;
    }

    private LitemallOrderGoods orderGoods(Integer productId, int number) {
        LitemallOrderGoods goods = new LitemallOrderGoods();
        goods.setProductId(productId);
        goods.setNumber((short) number);
        return goods;
    }

    private LitemallCouponUser coupon(Short status) {
        LitemallCouponUser coupon = new LitemallCouponUser();
        coupon.setStatus(status);
        coupon.setOrderId(1001);
        return coupon;
    }
}
