package org.linlinjava.litemall.tests.unit;

import org.junit.Test;
import org.linlinjava.litemall.wx.service.OrderTimeoutCompensationService;
import org.linlinjava.litemall.wx.task.OrderUnpaidTask;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class OrderUnpaidTaskUnitTest {

    @Test
    public void runDelegatesToTimeoutCompensationService() {
        OrderTimeoutCompensationService service = mock(OrderTimeoutCompensationService.class);
        OrderUnpaidTask task = new TestableOrderUnpaidTask(1001, service);

        task.run();

        verify(service).closeOrder(1001);
    }

    @Test(expected = IllegalStateException.class)
    public void runPropagatesCompensationFailure() {
        OrderTimeoutCompensationService service = mock(OrderTimeoutCompensationService.class);
        doThrow(new IllegalStateException("database unavailable"))
                .when(service).closeOrder(1001);

        new TestableOrderUnpaidTask(1001, service).run();
    }

    private static class TestableOrderUnpaidTask extends OrderUnpaidTask {
        private final OrderTimeoutCompensationService service;

        private TestableOrderUnpaidTask(Integer orderId, OrderTimeoutCompensationService service) {
            super(orderId, 0);
            this.service = service;
        }

        @Override
        protected OrderTimeoutCompensationService getCompensationService() {
            return service;
        }
    }
}
