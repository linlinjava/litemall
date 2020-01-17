package org.linlinjava.litemall.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 异步测试
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class AsyncTest {

    @Autowired
    AsyncTask task;

    @Test
    public void test() {
        task.asyncMethod();
        task.nonasyncMethod();
    }
}
