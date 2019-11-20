package org.linlinjava.litemall.core;

import org.junit.Test;

public class IntegerTest {
    @Test
    public void test() {
        Integer a = new Integer(512);
        int b = 512;
        Integer c = new Integer(512);
        System.out.println(a==b);
        System.out.println(a.equals(b));
        System.out.println(a == c);
        System.out.println(a.equals(c));
    }
}
