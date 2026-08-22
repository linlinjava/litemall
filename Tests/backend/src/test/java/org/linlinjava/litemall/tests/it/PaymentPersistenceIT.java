package org.linlinjava.litemall.tests.it;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;
import org.linlinjava.litemall.db.domain.LitemallGoodsProduct;
import org.linlinjava.litemall.db.domain.LitemallGroupon;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.service.LitemallCouponUserService;
import org.linlinjava.litemall.db.service.LitemallGoodsProductService;
import org.linlinjava.litemall.db.service.LitemallGrouponService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.util.CouponUserConstant;
import org.linlinjava.litemall.db.util.GrouponConstant;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MySQLContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentPersistenceIT.DbTestApplication.class)
@ContextConfiguration(initializers = PaymentPersistenceIT.Initializer.class)
public class PaymentPersistenceIT {

    @ClassRule
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("litemall_test")
            .withUsername("litemall")
            .withPassword("litemall123456")
            .withInitScript("db/litemall-it-schema.sql");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LitemallGoodsProductService productService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallCouponUserService couponUserService;
    @Autowired
    private LitemallGrouponService grouponService;

    @Before
    public void resetData() {
        jdbcTemplate.update("update litemall_goods_product set number = 5, update_time = now() where id = 1");
        jdbcTemplate.update("update litemall_order set order_status = 101, pay_id = null, pay_time = null, update_time = now() where id = 1");
        jdbcTemplate.update("update litemall_coupon_user set status = 1, used_time = now(), update_time = now(), deleted = 0 where id = 1");
        jdbcTemplate.update("update litemall_groupon set status = 1, deleted = 0 where id in (10, 11)");
        jdbcTemplate.update("update litemall_groupon set status = 0, deleted = 0 where id = 12");
        jdbcTemplate.update("update litemall_groupon set status = 1, deleted = 1 where id = 13");
    }

    @Test
    public void mallPayAc06BeIt001_stockCannotGoNegativeAndCanBeRestored() {
        assertEquals(1, productService.reduceStock(1, (short) 3));
        assertEquals(2, productService.findById(1).getNumber().intValue());

        assertEquals(0, productService.reduceStock(1, (short) 10));
        assertEquals(2, productService.findById(1).getNumber().intValue());

        assertEquals(1, productService.addStock(1, (short) 3));
        assertEquals(5, productService.findById(1).getNumber().intValue());
    }

    @Test
    public void mallPayAc05BeIt001_optimisticLockRejectsStaleOrderUpdate() {
        LitemallOrder staleCopy = orderService.findById(1);
        LitemallOrder freshCopy = orderService.findById(1);

        freshCopy.setOrderStatus(OrderUtil.STATUS_PAY);
        assertEquals(1, orderService.updateWithOptimisticLocker(freshCopy));
        jdbcTemplate.update("update litemall_order set update_time = '2026-07-11 10:00:30' where id = 1");

        staleCopy.setOrderStatus(OrderUtil.STATUS_SHIP);
        assertEquals(0, orderService.updateWithOptimisticLocker(staleCopy));
        assertEquals(OrderUtil.STATUS_PAY, orderService.findById(1).getOrderStatus());
    }

    @Test
    public void mallPayAc01BeIt001_paidOrderFieldsPersistWithOptimisticLock() {
        LitemallOrder order = orderService.findById(1);
        LocalDateTime paidAt = LocalDateTime.of(2026, 7, 11, 10, 0);

        order.setOrderStatus(OrderUtil.STATUS_PAY);
        order.setPayId("wxpay-001");
        order.setPayTime(paidAt);

        assertEquals(1, orderService.updateWithOptimisticLocker(order));

        LitemallOrder persisted = orderService.findById(1);
        assertEquals(OrderUtil.STATUS_PAY, persisted.getOrderStatus());
        assertEquals("wxpay-001", persisted.getPayId());
        assertNotNull(persisted.getPayTime());
        assertEquals(paidAt, persisted.getPayTime());
    }

    @Test
    public void mallPayAc09BeIt001_couponReleasePersistsUsableStatusForOrder() {
        List<LitemallCouponUser> coupons = couponUserService.findByOid(1);
        assertEquals(1, coupons.size());
        assertEquals(CouponUserConstant.STATUS_USED, coupons.get(0).getStatus());

        LitemallCouponUser couponUser = coupons.get(0);
        couponUser.setStatus(CouponUserConstant.STATUS_USABLE);
        assertEquals(1, couponUserService.update(couponUser));

        LitemallCouponUser persisted = couponUserService.findById(1);
        assertEquals(CouponUserConstant.STATUS_USABLE, persisted.getStatus());
        assertNotNull(persisted.getUpdateTime());
    }

    @Test
    public void mallPayAc10BeIt001_grouponQueriesIgnoreNoneAndDeletedRows() {
        List<LitemallGroupon> joinRecords = grouponService.queryJoinRecord(10);
        assertEquals(1, joinRecords.size());
        assertEquals(Integer.valueOf(11), joinRecords.get(0).getId());
        assertEquals(1, grouponService.countGroupon(10));

        LitemallGroupon byOrder = grouponService.queryByOrderId(2);
        byOrder.setStatus(GrouponConstant.STATUS_SUCCEED);
        assertEquals(1, grouponService.updateById(byOrder));

        assertEquals(GrouponConstant.STATUS_SUCCEED, grouponService.queryById(11).getStatus());
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                    "spring.datasource.druid.url=" + mysql.getJdbcUrl(),
                    "spring.datasource.druid.username=" + mysql.getUsername(),
                    "spring.datasource.druid.password=" + mysql.getPassword(),
                    "spring.datasource.druid.driver-class-name=com.mysql.cj.jdbc.Driver",
                    "pagehelper.helperDialect=mysql"
            ).applyTo(context.getEnvironment());
        }
    }

    @SpringBootApplication(scanBasePackages = "org.linlinjava.litemall.db")
    @MapperScan("org.linlinjava.litemall.db.dao")
    public static class DbTestApplication {
        public static void main(String[] args) {
            SpringApplication.run(DbTestApplication.class, args);
        }
    }
}
