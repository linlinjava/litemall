package org.linlinjava.litemall.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.core.express.ExpressService;
import org.linlinjava.litemall.core.express.dao.ExpressInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class ExpressTest {
    @Autowired
    private ExpressService expressService;

    @Test
    public void test() {
        ExpressInfo ei = null;
        try {
            ei = expressService.getExpressInfo("YTO", "800669400640887922");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print(ei);
    }
}
