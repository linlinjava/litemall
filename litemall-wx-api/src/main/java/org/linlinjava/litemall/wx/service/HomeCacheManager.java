package org.linlinjava.litemall.wx.service;

import java.util.Map;

/**
 * 简单缓存首页的数据
 */
public class HomeCacheManager {
    private static Map<String, Object> cacheData;

    /**
     * 缓存首页数据
     *
     * @param data
     */
    public static void loadData(Map<String, Object> data) {
        cacheData = data;
    }

    public static Map<String, Object> getCacheData() {
        return cacheData;
    }

    /**
     * 判断缓存中是否有数据
     *
     * @return
     */
    public static boolean hasData() {
        return cacheData != null;
    }

    /**
     * 清除缓存数据
     */
    public static void clear() {
        cacheData = null;
    }
}
