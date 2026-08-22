package org.linlinjava.litemall.tests.it;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.db.domain.LitemallCouponUser;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.service.LitemallCouponUserService;
import org.linlinjava.litemall.db.service.LitemallGoodsProductService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.util.CouponUserConstant;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.service.OrderTimeoutCompensationService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderTimeoutConsistencyIT.DbTestApplication.class)
@ContextConfiguration(initializers = OrderTimeoutConsistencyIT.Initializer.class)
public class OrderTimeoutConsistencyIT {

    @ClassRule
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("litemall_test")
            .withUsername("litemall")
            .withPassword("litemall123456")
            .withInitScript("db/litemall-it-schema.sql");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallGoodsProductService productService;
    @Autowired
    private LitemallCouponUserService couponUserService;
    @Autowired
    private OrderTimeoutCompensationService timeoutService;

    @Before
    public void resetData() {
        jdbcTemplate.update("delete from litemall_order_goods");
        jdbcTemplate.update("update litemall_goods_product set number = 5, update_time = now() where id = 1");
        jdbcTemplate.update("update litemall_order set order_status = 101, end_time = null, update_time = now() where id = 1");
        jdbcTemplate.update("update litemall_coupon_user set status = 1, used_time = now(), update_time = now(), deleted = 0 where id = 1");
        jdbcTemplate.update("insert into litemall_order_goods (id, order_id, goods_id, goods_name, goods_sn, product_id, number, price, specifications, pic_url, comment, add_time, update_time, deleted) values (1, 1, 100, '测试商品', 'goods-100', 1, 2, 100.00, '[\\\"standard\\\"]', '', 0, now(), now(), 0)");
    }

    @Test
    public void mallOrdTimeoutAc01BeIt001_commitsOrderAndAllResourceChanges() {
        timeoutService.closeOrder(1);

        LitemallOrder order = orderService.findById(1);
        assertEquals(OrderUtil.STATUS_AUTO_CANCEL, order.getOrderStatus());
        assertNotNull(order.getEndTime());
        assertEquals(7, productService.findById(1).getNumber().intValue());
        assertEquals(CouponUserConstant.STATUS_USABLE, couponUserService.findById(1).getStatus());
    }

    @Test
    public void mallOrdTimeoutAc02BeIt001_repeatedTaskDoesNotRepeatCompensation() {
        timeoutService.closeOrder(1);
        timeoutService.closeOrder(1);

        assertEquals(OrderUtil.STATUS_AUTO_CANCEL, orderService.findById(1).getOrderStatus());
        assertEquals(7, productService.findById(1).getNumber().intValue());
        assertEquals(CouponUserConstant.STATUS_USABLE, couponUserService.findById(1).getStatus());
    }

    @Test
    public void mallOrdTimeoutAc03BeIt001_stockFailureRollsBackOrderAndCoupon() {
        jdbcTemplate.update("update litemall_order_goods set product_id = 9999 where id = 1");

        boolean failed = false;
        try {
            timeoutService.closeOrder(1);
        } catch (RuntimeException expected) {
            failed = true;
        }
        if (!failed) {
            fail("stock failure must be visible to the caller");
        }

        assertEquals(OrderUtil.STATUS_CREATE, orderService.findById(1).getOrderStatus());
        assertEquals(5, productService.findById(1).getNumber().intValue());
        assertEquals(CouponUserConstant.STATUS_USED, couponUserService.findById(1).getStatus());
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
    @Import(OrderTimeoutCompensationService.class)
    @MapperScan("org.linlinjava.litemall.db.dao")
    public static class DbTestApplication {
        public static void main(String[] args) {
            SpringApplication.run(DbTestApplication.class, args);
        }
    }
}
