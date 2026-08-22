package org.linlinjava.litemall.tests.unit;

import org.junit.Before;
import org.junit.Test;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.service.LitemallCouponUserService;
import org.linlinjava.litemall.db.service.LitemallGoodsProductService;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.util.CouponUserConstant;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.service.WxOrderService;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class WxOrderLifecycleUnitTest {
    private WxOrderService service;
    private LitemallOrderService orderService;
    private LitemallOrderGoodsService orderGoodsService;
    private LitemallGoodsProductService productService;
    private LitemallCouponUserService couponUserService;
    private NotifyService notifyService;

    @Before
    public void setUp() {
        service = new WxOrderService();
        orderService = mock(LitemallOrderService.class);
        orderGoodsService = mock(LitemallOrderGoodsService.class);
        productService = mock(LitemallGoodsProductService.class);
        couponUserService = mock(LitemallCouponUserService.class);
        notifyService = mock(NotifyService.class);

        ReflectionTestUtils.setField(service, "orderService", orderService);
        ReflectionTestUtils.setField(service, "orderGoodsService", orderGoodsService);
        ReflectionTestUtils.setField(service, "productService", productService);
        ReflectionTestUtils.setField(service, "couponUserService", couponUserService);
        ReflectionTestUtils.setField(service, "notifyService", notifyService);
    }

    @Test
    public void mallPayAc09BeUt001_cancelUnpaidOrderRestoresStockAndReleasesCoupon() {
        LitemallOrder order = order(1, 1001, OrderUtil.STATUS_CREATE);
        LitemallOrderGoods orderGoods = orderGoods(1, 2);
        LitemallCouponUser couponUser = couponUser(501, CouponUserConstant.STATUS_USED);

        when(orderService.findById(1, 1001)).thenReturn(order);
        when(orderService.updateWithOptimisticLocker(order)).thenReturn(1);
        when(orderGoodsService.queryByOid(1001)).thenReturn(Collections.singletonList(orderGoods));
        when(productService.addStock(1, (short) 2)).thenReturn(1);
        when(couponUserService.findByOid(1001)).thenReturn(Collections.singletonList(couponUser));
        when(couponUserService.update(couponUser)).thenReturn(1);

        Map<String, Object> response = responseMap(service.cancel(1, "{\"orderId\":1001}"));

        assertEquals(0, response.get("errno"));
        assertEquals(OrderUtil.STATUS_CANCEL, order.getOrderStatus());
        assertNotNull(order.getEndTime());
        assertEquals(CouponUserConstant.STATUS_USABLE, couponUser.getStatus());
        assertNotNull(couponUser.getUpdateTime());
        verify(productService).addStock(1, (short) 2);
        verify(couponUserService).update(couponUser);
    }

    @Test
    public void mallPayAc09BeUt002_paidOrderCannotUseCancelCompensationPath() {
        LitemallOrder order = order(1, 1001, OrderUtil.STATUS_PAY);
        when(orderService.findById(1, 1001)).thenReturn(order);

        Map<String, Object> response = responseMap(service.cancel(1, "{\"orderId\":1001}"));

        assertEquals(725, response.get("errno"));
        assertEquals("订单不能取消", response.get("errmsg"));
        assertEquals(OrderUtil.STATUS_PAY, order.getOrderStatus());
        verify(orderService, never()).updateWithOptimisticLocker(order);
        verify(productService, never()).addStock(anyInt(), anyShort());
        verify(couponUserService, never()).findByOid(1001);
    }

    @Test
    public void mallPayAc12BeUt001_refundPaidOrderMovesToRefundStatusAndNotifiesOps() {
        LitemallOrder order = order(1, 1001, OrderUtil.STATUS_PAY);
        when(orderService.findById(1, 1001)).thenReturn(order);
        when(orderService.updateWithOptimisticLocker(order)).thenReturn(1);

        Map<String, Object> response = responseMap(service.refund(1, "{\"orderId\":1001}"));

        assertEquals(0, response.get("errno"));
        assertEquals(OrderUtil.STATUS_REFUND, order.getOrderStatus());
        verify(notifyService).notifyMail(eq("退款申请"), anyString());
    }

    @Test
    public void mallPayAc12BeUt002_unpaidOrderCannotApplyRefund() {
        LitemallOrder order = order(1, 1001, OrderUtil.STATUS_CREATE);
        when(orderService.findById(1, 1001)).thenReturn(order);

        Map<String, Object> response = responseMap(service.refund(1, "{\"orderId\":1001}"));

        assertEquals(725, response.get("errno"));
        assertEquals("订单不能取消", response.get("errmsg"));
        assertEquals(OrderUtil.STATUS_CREATE, order.getOrderStatus());
        verify(orderService, never()).updateWithOptimisticLocker(order);
        verify(notifyService, never()).notifyMail(anyString(), anyString());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> responseMap(Object response) {
        return (Map<String, Object>) response;
    }

    private LitemallOrder order(Integer userId, Integer orderId, Short status) {
        LitemallOrder order = new LitemallOrder();
        order.setId(orderId);
        order.setUserId(userId);
        order.setOrderSn("20260711000001");
        order.setOrderStatus(status);
        order.setMobile("13800000000");
        order.setUpdateTime(LocalDateTime.of(2026, 7, 11, 10, 0));
        return order;
    }

    private LitemallOrderGoods orderGoods(Integer productId, int number) {
        LitemallOrderGoods orderGoods = new LitemallOrderGoods();
        orderGoods.setProductId(productId);
        orderGoods.setNumber((short) number);
        return orderGoods;
    }

    private LitemallCouponUser couponUser(Integer id, Short status) {
        LitemallCouponUser couponUser = new LitemallCouponUser();
        couponUser.setId(id);
        couponUser.setStatus(status);
        couponUser.setOrderId(1001);
        return couponUser;
    }
}
