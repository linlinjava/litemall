package org.linlinjava.litemall.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.core.storage.QiniuStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class QiniuStorageTest {

    private final Log logger = LogFactory.getLog(QiniuStorageTest.class);
    @Autowired
    private QiniuStorage qiniuStorage;

    @Test
    public void test() throws IOException {
        String test = getClass().getClassLoader().getResource("litemall.png").getFile();
        File testFile = new File(test);
        qiniuStorage.store(new FileInputStream(test), testFile.length(), "image/png", "litemall.png");
        Resource resource = qiniuStorage.loadAsResource("litemall.png");
        String url = qiniuStorage.generateUrl("litemall.png");
        logger.info("test file " + test);
        logger.info("store file " + resource.getURI());
        logger.info("generate url " + url);
    }

}