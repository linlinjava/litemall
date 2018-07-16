package org.linlinjava.litemall.os;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.os.config.ObjectStorageConfig;
import org.linlinjava.litemall.os.service.TencentOsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.FileCopyUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TosTest {
    @Autowired
    private TencentOsService tencentOsService;

    @Test
    public void test() throws IOException {
        String test = getClass().getClassLoader().getResource("litemall.png").getFile();
        byte[] content =  (byte[])FileCopyUtils.copyToByteArray(new FileInputStream(test));
        MockMultipartFile mockMultipartFile = new MockMultipartFile("litemall.png", "litemall.png", "image/png", content);
        tencentOsService.store(mockMultipartFile, "tos.png");
        Resource resource = tencentOsService.loadAsResource("tos.png");
        String url = tencentOsService.generateUrl("tos.png");
        System.out.println("test file " + test);
        System.out.println("store file " + resource.getURI());
        System.out.println("generate url " + url);

//        tencentOsService.delete("tos.png");
    }

}