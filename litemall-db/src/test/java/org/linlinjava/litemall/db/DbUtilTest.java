package org.linlinjava.litemall.db;

import org.junit.Test;
import org.linlinjava.litemall.db.util.DbUtil;

import java.io.File;

public class DbUtilTest {
    @Test
    public void testBackup() {
        File file = new File("test.sql");
        DbUtil.backup(file, "litemall", "litemall123456", "litemall");
    }

//    这个测试用例会重置litemall数据库，所以比较危险，请开发者注意
//    @Test
    public void testLoad() {
        File file = new File("test.sql");
        DbUtil.load(file, "litemall", "litemall123456", "litemall");
    }
}
