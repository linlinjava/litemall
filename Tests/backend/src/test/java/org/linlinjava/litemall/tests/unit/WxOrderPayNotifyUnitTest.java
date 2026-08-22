package org.linlinjava.litemall.tests.unit;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import org.junit.Before;
import org.junit.Test;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.core.qcode.QCodeService;
import org.linlinjava.litemall.core.system.SystemConfig;
import org.linlinjava.litemall.core.task.TaskService;
import org.linlinjava.litemall.db.domain.LitemallGroupon;
import org.linlinjava.litemall.db.domain.LitemallGrouponRules;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.service.LitemallGrouponService;
import org.linlinjava.litemall.db.service.LitemallGrouponRulesService;
import org.linlinjava.litemall.db.util.GrouponConstant;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.service.WxOrderService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class WxOrderPayNotifyUnitTest {
    private WxOrderService service;
    private WxPayService wxPayService;
    private LitemallOrderService orderService;
    private NotifyService notifyService;
    private TaskService taskService;
    private LitemallGrouponService grouponService;
    private LitemallGrouponRulesService grouponRulesService;
    private QCodeService qCodeService;

    @Before
    public void setUp() {
        service = new WxOrderService();
        wxPayService = mock(WxPayService.class);
        orderService = mock(LitemallOrderService.class);
        notifyService = mock(NotifyService.class);
        taskService = mock(TaskService.class);
        grouponService = mock(LitemallGrouponService.class);
        grouponRulesService = mock(LitemallGrouponRulesService.class);
        qCodeService = mock(QCodeService.class);

        ReflectionTestUtils.setField(service, "wxPayService", wxPayService);
        ReflectionTestUtils.setField(service, "orderService", orderService);
        ReflectionTestUtils.setField(service, "notifyService", notifyService);
        ReflectionTestUtils.setField(service, "taskService", taskService);
        ReflectionTestUtils.setField(service, "grouponService", grouponService);
        ReflectionTestUtils.setField(service, "grouponRulesService", grouponRulesService);
        ReflectionTestUtils.setField(service, "qCodeService", qCodeService);
        SystemConfig.setConfigs(Collections.singletonMap(SystemConfig.LITEMALL_ORDER_UNPAID, "30"));
    }

    @Test
    public void mallPayAc01BeUt001_successCallbackMarksUnpaidOrderAsPaid() throws Exception {
        LitemallOrder order = unpaidOrder("20260711000001", "100.00");
        when(wxPayService.parseOrderNotifyResult(anyString())).thenReturn(notifyResult("20260711000001", "wxpay-001", 10000));
        when(orderService.findBySn("20260711000001")).thenReturn(order);
        when(orderService.updateWithOptimisticLocker(order)).thenReturn(1);

        Object response = service.payNotify(xmlRequest(), new MockHttpServletResponse());

        assertTrue(response.toString().contains("SUCCESS"));
        assertEquals(OrderUtil.STATUS_PAY, order.getOrderStatus());
        assertEquals("wxpay-001", order.getPayId());
        verify(orderService).updateWithOptimisticLocker(order);
        verify(taskService).removeTask(any());
        verify(notifyService).notifyMail(eq("新订单通知"), anyString());
    }

    @Test
    public void mallPayAc02BeUt001_duplicateCallbackReturnsSuccessWithoutSecondUpdate() throws Exception {
        LitemallOrder order = unpaidOrder("20260711000001", "100.00");
        order.setOrderStatus(OrderUtil.STATUS_PAY);
        when(wxPayService.parseOrderNotifyResult(anyString())).thenReturn(notifyResult("20260711000001", "wxpay-001", 10000));
        when(orderService.findBySn("20260711000001")).thenReturn(order);

        Object response = service.payNotify(xmlRequest(), new MockHttpServletResponse());

        assertTrue(response.toString().contains("SUCCESS"));
        verify(orderService, never()).updateWithOptimisticLocker(any());
        verify(taskService, never()).removeTask(any());
    }

    @Test
    public void mallPayAc03BeUt001_amountMismatchFailsAndDoesNotMutateOrder() throws Exception {
        LitemallOrder order = unpaidOrder("20260711000001", "100.00");
        when(wxPayService.parseOrderNotifyResult(anyString())).thenReturn(notifyResult("20260711000001", "wxpay-001", 9900));
        when(orderService.findBySn("20260711000001")).thenReturn(order);

        Object response = service.payNotify(xmlRequest(), new MockHttpServletResponse());

        assertTrue(response.toString().contains("FAIL"));
        assertEquals(OrderUtil.STATUS_CREATE, order.getOrderStatus());
        verify(orderService, never()).updateWithOptimisticLocker(any());
        verify(taskService, never()).removeTask(any());
    }

    @Test
    public void mallPayAc04BeUt001_parseFailureReturnsFailBeforeOrderLookup() throws Exception {
        when(wxPayService.parseOrderNotifyResult(anyString())).thenThrow(new WxPayException("signature invalid"));

        Object response = service.payNotify(xmlRequest(), new MockHttpServletResponse());

        assertTrue(response.toString().contains("FAIL"));
        assertTrue(response.toString().contains("signature invalid"));
        verify(orderService, never()).findBySn(anyString());
        verify(orderService, never()).updateWithOptimisticLocker(any());
        verify(taskService, never()).removeTask(any());
        verify(notifyService, never()).notifyMail(anyString(), anyString());
    }

    @Test
    public void mallPayAc04BeUt002_missingOrderReturnsFailWithoutMutation() throws Exception {
        when(wxPayService.parseOrderNotifyResult(anyString())).thenReturn(notifyResult("20260711000002", "wxpay-002", 10000));
        when(orderService.findBySn("20260711000002")).thenReturn(null);

        Object response = service.payNotify(xmlRequest(), new MockHttpServletResponse());

        assertTrue(response.toString().contains("FAIL"));
        assertTrue(response.toString().contains("订单不存在"));
        verify(orderService, never()).updateWithOptimisticLocker(any());
        verify(taskService, never()).removeTask(any());
        verify(notifyService, never()).notifyMail(anyString(), anyString());
    }

    @Test
    public void mallPayAc05BeUt001_optimisticLockFailureStopsNotificationAndTaskRemoval() throws Exception {
        LitemallOrder order = unpaidOrder("20260711000001", "100.00");
        when(wxPayService.parseOrderNotifyResult(anyString())).thenReturn(notifyResult("20260711000001", "wxpay-001", 10000));
        when(orderService.findBySn("20260711000001")).thenReturn(order);
        when(orderService.updateWithOptimisticLocker(order)).thenReturn(0);

        Object response = service.payNotify(xmlRequest(), new MockHttpServletResponse());

        assertTrue(response.toString().contains("FAIL"));
        assertTrue(response.toString().contains("更新数据已失效"));
        verify(orderService).updateWithOptimisticLocker(order);
        verify(taskService, never()).removeTask(any());
        verify(notifyService, never()).notifyMail(anyString(), anyString());
    }

    @Test
    public void mallPayAc10BeUt001_paidJoinCompletesGrouponWhenMemberThresholdReached() throws Exception {
        LitemallOrder order = unpaidOrder("20260711000001", "100.00");
        LitemallGroupon joiner = groupon(12, 1001, 10, 7, GrouponConstant.STATUS_NONE);
        LitemallGroupon source = groupon(10, 999, 0, 7, GrouponConstant.STATUS_ON);
        LitemallGrouponRules rules = grouponRules(7, 2);

        when(wxPayService.parseOrderNotifyResult(anyString())).thenReturn(notifyResult("20260711000001", "wxpay-001", 10000));
        when(orderService.findBySn("20260711000001")).thenReturn(order);
        when(orderService.updateWithOptimisticLocker(order)).thenReturn(1);
        when(grouponService.queryByOrderId(1001)).thenReturn(joiner);
        when(grouponRulesService.findById(7)).thenReturn(rules);
        when(grouponService.updateById(any(LitemallGroupon.class))).thenReturn(1);
        when(grouponService.queryJoinRecord(10)).thenReturn(Collections.singletonList(joiner));
        when(grouponService.queryById(10)).thenReturn(source);

        Object response = service.payNotify(xmlRequest(), new MockHttpServletResponse());

        assertTrue(response.toString().contains("SUCCESS"));
        assertEquals(GrouponConstant.STATUS_SUCCEED, joiner.getStatus());
        assertEquals(GrouponConstant.STATUS_SUCCEED, source.getStatus());
        verify(grouponService, times(3)).updateById(any(LitemallGroupon.class));
    }

    @Test
    public void mallPayAc10BeUt002_groupOwnerCreatesShareImageAfterPayment() throws Exception {
        LitemallOrder order = unpaidOrder("20260711000001", "100.00");
        LitemallGroupon owner = groupon(10, 1001, 0, 7, GrouponConstant.STATUS_NONE);
        LitemallGrouponRules rules = grouponRules(7, 2);
        rules.setGoodsName("测试团购商品");
        rules.setPicUrl("https://example.test/goods.png");

        when(wxPayService.parseOrderNotifyResult(anyString())).thenReturn(notifyResult("20260711000001", "wxpay-001", 10000));
        when(orderService.findBySn("20260711000001")).thenReturn(order);
        when(orderService.updateWithOptimisticLocker(order)).thenReturn(1);
        when(grouponService.queryByOrderId(1001)).thenReturn(owner);
        when(grouponRulesService.findById(7)).thenReturn(rules);
        when(qCodeService.createGrouponShareImage("测试团购商品", "https://example.test/goods.png", owner)).thenReturn("/storage/groupon-share.png");
        when(grouponService.updateById(owner)).thenReturn(1);
        when(grouponService.queryJoinRecord(0)).thenReturn(Collections.emptyList());

        Object response = service.payNotify(xmlRequest(), new MockHttpServletResponse());

        assertTrue(response.toString().contains("SUCCESS"));
        assertEquals(GrouponConstant.STATUS_ON, owner.getStatus());
        assertEquals("/storage/groupon-share.png", owner.getShareUrl());
        verify(qCodeService).createGrouponShareImage("测试团购商品", "https://example.test/goods.png", owner);
    }

    private MockHttpServletRequest xmlRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        request.setContent("<xml><return_code>SUCCESS</return_code></xml>".getBytes(StandardCharsets.UTF_8));
        return request;
    }

    private WxPayOrderNotifyResult notifyResult(String orderSn, String transactionId, int totalFee) {
        WxPayOrderNotifyResult result = new WxPayOrderNotifyResult();
        result.setReturnCode(WxPayConstants.ResultCode.SUCCESS);
        result.setResultCode(WxPayConstants.ResultCode.SUCCESS);
        result.setOutTradeNo(orderSn);
        result.setTransactionId(transactionId);
        result.setTotalFee(totalFee);
        return result;
    }

    private LitemallOrder unpaidOrder(String orderSn, String actualPrice) {
        LitemallOrder order = new LitemallOrder();
        order.setId(1001);
        order.setOrderSn(orderSn);
        order.setOrderStatus(OrderUtil.STATUS_CREATE);
        order.setActualPrice(new BigDecimal(actualPrice));
        order.setMobile("13800000000");
        order.setUpdateTime(LocalDateTime.now());
        return order;
    }

    private LitemallGroupon groupon(Integer id, Integer orderId, Integer grouponId, Integer rulesId, Short status) {
        LitemallGroupon groupon = new LitemallGroupon();
        groupon.setId(id);
        groupon.setOrderId(orderId);
        groupon.setGrouponId(grouponId);
        groupon.setRulesId(rulesId);
        groupon.setStatus(status);
        return groupon;
    }

    private LitemallGrouponRules grouponRules(Integer id, Integer discountMember) {
        LitemallGrouponRules rules = new LitemallGrouponRules();
        rules.setId(id);
        rules.setDiscountMember(discountMember);
        return rules;
    }
}
