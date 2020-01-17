package org.linlinjava.litemall.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class DbConfigTest {
    @Autowired
    Environment environment;

    @Test
    public void test() {
        System.out.println(environment.getProperty("spring.datasource.druid.url"));
    }
}
