package org.linlinjava.litemall.tests.unit;

import org.junit.Test;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.util.OrderHandleOption;
import org.linlinjava.litemall.db.util.OrderUtil;

import static org.junit.Assert.*;

public class OrderUtilUnitTest {

    @Test
    public void mallPayAc02BeUt001_hasPayedReturnsFalseOnlyForUnpaidAndCancelledOrders() {
        assertFalse(OrderUtil.hasPayed(order(OrderUtil.STATUS_CREATE)));
        assertFalse(OrderUtil.hasPayed(order(OrderUtil.STATUS_CANCEL)));
        assertFalse(OrderUtil.hasPayed(order(OrderUtil.STATUS_AUTO_CANCEL)));

        assertTrue(OrderUtil.hasPayed(order(OrderUtil.STATUS_PAY)));
        assertTrue(OrderUtil.hasPayed(order(OrderUtil.STATUS_SHIP)));
        assertTrue(OrderUtil.hasPayed(order(OrderUtil.STATUS_CONFIRM)));
    }

    @Test
    public void mallPayAc01BeUt002_createStatusAllowsPayAndCancelOnly() {
        OrderHandleOption option = OrderUtil.build(order(OrderUtil.STATUS_CREATE));

        assertTrue(option.isPay());
        assertTrue(option.isCancel());
        assertFalse(option.isRefund());
        assertFalse(option.isConfirm());
        assertFalse(option.isDelete());
    }

    @Test
    public void mallPayAc01BeUt003_paidStatusAllowsRefundButNotPayAgain() {
        OrderHandleOption option = OrderUtil.build(order(OrderUtil.STATUS_PAY));

        assertTrue(option.isRefund());
        assertFalse(option.isPay());
        assertFalse(option.isCancel());
        assertFalse(option.isConfirm());
    }

    private LitemallOrder order(Short status) {
        LitemallOrder order = new LitemallOrder();
        order.setOrderStatus(status);
        return order;
    }
}
