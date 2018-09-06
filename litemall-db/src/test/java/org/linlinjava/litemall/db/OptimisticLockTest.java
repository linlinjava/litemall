package org.linlinjava.litemall.db;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.linlinjava.litemall.db.dao.LitemallSystemMapper;
import org.linlinjava.litemall.db.domain.LitemallSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/*
   main线程先select，然后睡眠1s，最后做update
   another线程则是先select，然后做update

   main:        select                           update 2
   another:               select     update 3

   如果没有乐观锁，那么最后main线程的update操作是成功的，最终数据库保存的值是2；
   如果设置乐观锁，那么最后main线程的update操作是失败的，最终数据库保存的值是3。

   在一些业务过程中，需要采用乐观锁，这样可以保证数据更新时不会出现问题。
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class OptimisticLockTest {

    @Autowired
    private LitemallSystemMapper systemMapper;

    private Integer unlockId;
    private Integer lockId;

    @Before
    public void before() {
        LitemallSystem unlockSystemConfig = new LitemallSystem();
        unlockSystemConfig.setKeyName("test-unlocksystem-key");
        unlockSystemConfig.setKeyValue("test-unlocksystem-value-1");
        systemMapper.insertSelective(unlockSystemConfig);
        unlockId = unlockSystemConfig.getId();

        LitemallSystem lockSystemConfig = new LitemallSystem();
        lockSystemConfig.setKeyName("test-locksystem-key");
        lockSystemConfig.setKeyValue("test-locksystem-value-1");
        systemMapper.insertSelective(lockSystemConfig);
        lockId = lockSystemConfig.getId();
    }

    @After
    public void after() {
        systemMapper.deleteByPrimaryKey(unlockId);
        unlockId = null;
        systemMapper.deleteByPrimaryKey(lockId);
        lockId = null;
    }


    @Test
    public void runWithOptimisticLock() {
        LitemallSystem mainSystem = systemMapper.selectByPrimaryKey(lockId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                LitemallSystem anotherSystem = systemMapper.selectByPrimaryKey(lockId);

                anotherSystem.setKeyValue("test-locksystem-value-3");
                systemMapper.updateWithVersionByPrimaryKey(anotherSystem.getVersion(), anotherSystem);
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mainSystem.setKeyValue("test-locksystem-value-2");
        int updates = systemMapper.updateWithVersionByPrimaryKey(mainSystem.getVersion(), mainSystem);
        Assert.assertEquals(updates, 0);

        mainSystem = systemMapper.selectByPrimaryKey(lockId);
        Assert.assertNotEquals(mainSystem.getKeyValue(), "test-locksystem-value-2");
        Assert.assertEquals(mainSystem.getKeyValue(), "test-locksystem-value-3");
    }

    @Test
    public void runWithoutOptimisticLock() {
        LitemallSystem mainSystem = systemMapper.selectByPrimaryKey(unlockId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                LitemallSystem anotherSystem = systemMapper.selectByPrimaryKey(unlockId);

                anotherSystem.setKeyValue("test-unlocksystem-value-3");
                systemMapper.updateByPrimaryKey(anotherSystem);
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mainSystem.setKeyValue("test-unlocksystem-value-2");
        int updates = systemMapper.updateByPrimaryKey(mainSystem);
        Assert.assertEquals(updates, 1);

        mainSystem = systemMapper.selectByPrimaryKey(unlockId);
        Assert.assertEquals(mainSystem.getKeyValue(), "test-unlocksystem-value-2");
        Assert.assertNotEquals(mainSystem.getKeyValue(), "test-unlocksystem-value-3");
    }
}

