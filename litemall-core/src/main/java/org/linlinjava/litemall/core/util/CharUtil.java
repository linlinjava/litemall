package org.linlinjava.litemall.core.util;

import java.util.Random;

/**
 * 随机字符串工具类。
 */
public class CharUtil {

    /**
     * 生成指定长度的随机字符串（包含小写字母和数字）。
     *
     * @param num 随机字符串的长度
     * @return 随机字符串
     */
    public static String getRandomString(Integer num) {
        /* 随机字符池：小写字母与数字 */
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        /* 循环从字符池中随机取 num 个字符拼接 */
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的随机数字字符串。
     *
     * @param num 随机字符串的长度
     * @return 随机数字字符串
     */
    public static String getRandomNum(Integer num) {
        /* 随机字符池：仅数字 */
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        /* 循环从数字池中随机取 num 个字符拼接 */
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
