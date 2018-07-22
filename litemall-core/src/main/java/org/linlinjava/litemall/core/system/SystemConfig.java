package org.linlinjava.litemall.core.system;

import java.math.BigDecimal;

/**
 * 系统设置
 */

public class SystemConfig extends BaseConfig {
    public static final String PRE_FIX = "litemall.system.";

    public static Integer getNewLimit() {
        return getConfigInt(PRE_FIX + "indexlimit.new");
    }

    public static Integer getHotLimit() {
        return getConfigInt(PRE_FIX + "indexlimit.hot");
    }

    public static Integer getBrandLimit() {
        return getConfigInt(PRE_FIX + "indexlimit.brand");
    }

    public static Integer getTopicLimit() {
        return getConfigInt(PRE_FIX + "indexlimit.topic");
    }

    public static Integer getCatlogListLimit() {
        return getConfigInt(PRE_FIX + "indexlimit.catloglist");
    }

    public static Integer getCatlogMoreLimit() {
        return getConfigInt(PRE_FIX + "indexlimit.catloggood");
    }

    public static String getHotBannerTitle() {
        return getConfig(PRE_FIX + "banner.hot.title");
    }

    public static String getNewBannerTitle() {
        return getConfig(PRE_FIX + "banner.new.title");
    }

    public static String getHotImageUrl() {
        return getConfig(PRE_FIX + "banner.hot.imageurl");
    }

    public static String getNewImageUrl() {
        return getConfig(PRE_FIX + "banner.new.imageurl");
    }

    public static BigDecimal getFreight() {
        return getConfigBigDec(PRE_FIX + "freight.value");
    }

    public static BigDecimal getFreightLimit() {
        return getConfigBigDec(PRE_FIX + "freight.limit");
    }

    @Override
    public String getPrefix() {
        return PRE_FIX;
    }
}