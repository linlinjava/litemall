package org.linlinjava.litemall.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.core.notify.NotifyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 测试短信发送服务
 *
 * 注意LitemallNotifyService采用异步线程操作
 * 因此测试的时候需要睡眠一会儿，保证任务执行
 *
 * 开发者需要确保：
 * 1. 在腾讯云短信平台设置短信签名和短信模板notify.properties已经设置正确
 * 2. 在腾讯云短信平台设置短信签名和短信模板
 * 3. 在当前测试类设置好正确的手机号码
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SmsTest {

    @Autowired
    private NotifyService notifyService;

    @Test
    public void testSingle() {
        String phone = "xxxxxxxxxxx";
        // 这里的短信签名必须在短信管理平台内设置正确并且相符合
        String msg = "【xxx】你的验证码为:123456，请与2分钟内填写，如非本人操作，请忽略本短信。";
        notifyService.notifySms(phone, msg);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCaptcha() {
        String phone = "xxxxxxxxxxx";
        String[] params = new String[] {"123456"};

        notifyService.notifySmsTemplate(phone, NotifyType.CAPTCHA, params);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPaySucceed() {
        String phone = "xxxxxxxxxxx";
        String[] params = new String[] {"123456"};

        notifyService.notifySmsTemplate(phone, NotifyType.PAY_SUCCEED, params);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
