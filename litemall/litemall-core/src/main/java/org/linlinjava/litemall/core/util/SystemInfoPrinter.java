package org.linlinjava.litemall.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class SystemInfoPrinter {

    private static final Log logger = LogFactory.getLog(SystemInfoPrinter.class);
    public static final String CREATE_PART_COPPER = "XOXOXOXOX";

    private static int maxSize = 0;

    public static void printInfo(String title, Map<String, String> infos) {
        setMaxSize(infos);

        printHeader(title);

        for (Map.Entry<String, String> entry : infos.entrySet()) {
            printLine(entry.getKey(), entry.getValue());
        }

        printEnd();
    }

    private static void setMaxSize(Map<String, String> infos) {
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            if (entry.getValue() == null)
                continue;

            int size = entry.getKey().length() + entry.getValue().length();

            if (size > maxSize)
                maxSize = size;
        }

        maxSize = maxSize + 30;
    }

    private static void printHeader(String title) {
        logger.info(getLineCopper());
        logger.info("");
        logger.info("              " + title);
        logger.info("");
    }

    private static void printEnd() {
        logger.info("  ");
        logger.info(getLineCopper());
    }

    private static String getLineCopper() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxSize; i++) {
            sb.append("=");
        }

        return sb.toString();
    }

    private static void printLine(String head, String line) {
        if (line == null)
            return;

        if (head.startsWith(CREATE_PART_COPPER)) {
            logger.info("");
            logger.info("    [[  " + line + "  ]]");
            logger.info("");
        } else {
            logger.info("    " + head + "        ->        " + line);
        }
    }
}
