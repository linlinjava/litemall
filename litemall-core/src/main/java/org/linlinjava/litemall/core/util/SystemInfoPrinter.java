package org.linlinjava.litemall.core.util;

import java.util.Map;

public class SystemInfoPrinter {
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
        System.out.println(getLineCopper());
        System.out.println("");
        System.out.println("              " + title);
        System.out.println("");
    }

    private static void printEnd() {
        System.out.println("  ");
        System.out.println(getLineCopper());
    }

    private static String getLineCopper() {
        String copper = "";
        for (int i = 0; i < maxSize; i++) {
            copper += "=";
        }

        return copper;
    }

    private static void printLine(String head, String line) {
        if (line == null)
            return;

        if (head.startsWith(CREATE_PART_COPPER)) {
            System.out.println("");
            System.out.println("    [[  " + line + "  ]]");
            System.out.println("");
        } else {
            System.out.println("    " + head + "        ->        " + line);
        }
    }
}
