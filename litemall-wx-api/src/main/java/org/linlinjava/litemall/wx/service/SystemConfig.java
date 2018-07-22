package org.linlinjava.litemall.wx.service;

import java.math.BigDecimal;

/**
 * 系统设置
 */

public class SystemConfig {
    public static Integer getNewLimit() {
        return ConfigService.getCfg().getSysValueInt("indexlimit.new");
    }

    public static Integer getHotLimit() {
        return ConfigService.getCfg().getSysValueInt("indexlimit.hot");
    }

    public static Integer getBrandLimit() {
        return ConfigService.getCfg().getSysValueInt("indexlimit.brand");
    }

    public static Integer getTopicLimit() {
        return ConfigService.getCfg().getSysValueInt("indexlimit.topic");
    }

    public static Integer getCatlogListLimit() {
        return ConfigService.getCfg().getSysValueInt("indexlimit.catloglist");
    }

    public static Integer getCatlogMoreLimit() {
        return ConfigService.getCfg().getSysValueInt("indexlimit.catloggood");
    }

    public static String getHotBannerTitle() {
        return ConfigService.getCfg().getSysValue("banner.hot.title");
    }

    public static String getNewBannerTitle() {
        return ConfigService.getCfg().getSysValue("banner.new.title");
    }

    public static String getHotImageUrl() {
        return ConfigService.getCfg().getSysValue("banner.hot.imageurl");
    }

    public static String getNewImageUrl() {
        return ConfigService.getCfg().getSysValue("banner.new.imageurl");
    }

    public static BigDecimal getFreight() {
        return new BigDecimal(ConfigService.getCfg().getSysValue("freight.value"));
    }

    public static BigDecimal getFreightLimit() {
        return new BigDecimal(ConfigService.getCfg().getSysValue("freight.limit"));
    }
}
