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
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WxTemplateTest {

    @Autowired
    private NotifyService notifyService;

    @Test
    public void testPaySucceed() {
        String touser = "xx";
        String[] params = new String[]{"xxx"};
        notifyService.notifyWxTemplate(touser, NotifyType.PAY_SUCCEED, params);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
