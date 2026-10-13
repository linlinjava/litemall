package org.linlinjava.litemall.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * 系统信息打印工具类。
 * <p>
 * 用于在应用启动时以统一格式将系统配置信息打印到日志。
 */
public class SystemInfoPrinter {

    private static final Log logger = LogFactory.getLog(SystemInfoPrinter.class);
    public static final String CREATE_PART_COPPER = "XOXOXOXOX";

    /** 打印行的最大宽度，用于生成对齐的分隔横线 */
    private static int maxSize = 0;

    /**
     * 打印系统信息。
     *
     * @param title 信息标题
     * @param infos 键值对形式的信息集合
     */
    public static void printInfo(String title, Map<String, String> infos) {
        /* 先根据内容计算最大行宽 */
        setMaxSize(infos);

        /* 依次打印头部、各信息行、结尾 */
        printHeader(title);

        for (Map.Entry<String, String> entry : infos.entrySet()) {
            printLine(entry.getKey(), entry.getValue());
        }

        printEnd();
    }

    /**
     * 计算打印每行内容的最大宽度。
     *
     * @param infos 键值对形式的信息集合
     */
    private static void setMaxSize(Map<String, String> infos) {
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            if (entry.getValue() == null)
                continue;

            int size = entry.getKey().length() + entry.getValue().length();

            if (size > maxSize)
                maxSize = size;
        }

        /* 额外留 30 个字符的空隙，保证横线不紧贴内容 */
        maxSize = maxSize + 30;
    }

    /**
     * 打印信息头部。
     *
     * @param title 信息标题
     */
    private static void printHeader(String title) {
        logger.info(getLineCopper());
        logger.info("");
        logger.info("              " + title);
        logger.info("");
    }

    /**
     * 打印信息结尾。
     */
    private static void printEnd() {
        logger.info("  ");
        logger.info(getLineCopper());
    }

    /**
     * 生成一条由等号组成的横线。
     *
     * @return 横线字符串
     */
    private static String getLineCopper() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxSize; i++) {
            sb.append("=");
        }

        return sb.toString();
    }

    /**
     * 打印一行信息。
     *
     * @param head 键
     * @param line 值
     */
    private static void printLine(String head, String line) {
        if (line == null)
            return;

        /* 分区标题（以特殊前缀开头）单独高亮打印 */
        if (head.startsWith(CREATE_PART_COPPER)) {
            logger.info("");
            logger.info("    [[  " + line + "  ]]");
            logger.info("");
        } else {
            /* 普通键值对：key -> value */
            logger.info("    " + head + "        ->        " + line);
        }
    }
}
