package org.linlinjava.litemall.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期格式化工具类
 */
public class DateTimeUtil {

    /**
     * 将日期时间格式化为 "yyyy年MM月dd日 HH:mm:ss" 形式的字符串。
     *
     * @param dateTime 待格式化的日期时间
     * @return 格式化后的日期时间字符串
     */
    public static String getDateTimeDisplayString(LocalDateTime dateTime) {
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        String strDate2 = dtf2.format(dateTime);

        return strDate2;
    }
}
