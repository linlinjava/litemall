package org.linlinjava.litemall.wx;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WxConfigTest {
    @Autowired
    private WxPayService wxPayService;

    @Test
    public void test() {
        WxPayConfig wxPayConfig = wxPayService.getConfig();
        System.out.println(wxPayConfig.getMchId() + " " + wxPayConfig.getMchKey());
    }

}
