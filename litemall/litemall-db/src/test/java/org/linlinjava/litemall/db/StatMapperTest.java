package org.linlinjava.litemall.db;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.db.dao.StatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Map;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class StatMapperTest {

    @Autowired
    private StatMapper statMapper;

    @Test
    public void testUser() {
        List<Map> result = statMapper.statUser();
        for (Map m : result) {
            m.forEach((k, v) -> System.out.println("key:value = " + k + ":" + v));
        }
    }

    @Test
    public void testOrder() {
        List<Map> result = statMapper.statOrder();
        for (Map m : result) {
            m.forEach((k, v) -> System.out.println("key:value = " + k + ":" + v));
        }
    }

    @Test
    public void testGoods() {
        List<Map> result = statMapper.statGoods();
        for (Map m : result) {
            m.forEach((k, v) -> System.out.println("key:value = " + k + ":" + v));
        }
    }

}
