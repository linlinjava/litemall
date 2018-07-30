package org.linlinjava.litemall.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 测试邮件发送服务
 *
 * 注意LitemallNotifyService采用异步线程操作
 * 因此测试的时候需要睡眠一会儿，保证任务执行
 *
 *  开发者需要确保：
 *  1. 在相应的邮件服务器设置正确notify.properties已经设置正确
 *  2. 在相应的邮件服务器设置正确
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MailTest {

    @Autowired
    private NotifyService notifyService;

    @Test
    public void testMail() {
        notifyService.notifyMail("订单信息", "订单1111111已付款，请发货");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
