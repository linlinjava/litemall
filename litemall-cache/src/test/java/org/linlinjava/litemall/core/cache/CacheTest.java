package org.linlinjava.litemall.core.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class CacheTest {
    @Autowired
    Testor testor;

    @Test
    public void cacheEnableTest(){

        int i1 = testor.increateAndGet();
        int i2 = testor.increateAndGet();
        int i3 = testor.increateAndGet();
        assert i1==0;
        assert i2==0;
        assert i3==0;
    }
}
