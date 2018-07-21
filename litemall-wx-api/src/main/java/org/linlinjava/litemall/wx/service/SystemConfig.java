package org.linlinjava.litemall.wx.service;

import org.linlinjava.litemall.db.domain.LitemallSystem;
import org.linlinjava.litemall.db.service.LitemallSystemCfgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 系统通用设置服务类，注意用 getSystemConfig() 方法直接获取初始化的静态实例，重新读取配置调用 inist() 方法
 */
@Component
public class SystemConfig {
    public final static int LIMIT_NEW = 10001;
    public final static int LIMIT_HOT = 10002;
    public final static int LIMIT_BRAND = 10003;
    public final static int LIMIT_TOPIC = 10004;
    public final static int LIMIT_CALLIST = 10005;
    public final static int LIMIT_CALGOOD = 10006;

    public final static int BANNER_NEW = 11001;
    public final static int BANNER_HOT = 11002;

    public final static int BANNER_TITLE = 11101;
    public final static int BANNER_IMAGE = 11102;

    @Autowired
    private LitemallSystemCfgService systemCfgService;
    private List<LitemallSystem> systemConfigs;

    private LitemallSystem indexLimitCfg;
    private LitemallSystem newBannerCfg;
    private LitemallSystem hotBannerCfg;

    private static SystemConfig systemConfig;

    public static SystemConfig getSystemConfig() {
        return systemConfig;
    }

    @PostConstruct
    public void inist() {
        systemConfig = this;
        systemConfig.systemConfigs = systemCfgService.queryAll();
        systemConfig.inistConfigs();
    }

    public String getBannerInfo(final int banner, final int field) {
        LitemallSystem cfg = null;
        switch (banner) {
            case BANNER_NEW:
                cfg = newBannerCfg;
                break;
            case BANNER_HOT:
                cfg = hotBannerCfg;
                break;
        }

        if (cfg != null) {
            switch (field) {
                case BANNER_TITLE:
                    return cfg.getBaseValue();
                case BANNER_IMAGE:
                    return cfg.getExtraValue1();
            }
        }

        return null;
    }


    public Integer getIndexLimit(final int arg) {
        switch (arg) {
            case LIMIT_NEW:
                return Integer.parseInt(indexLimitCfg.getExtraValue1());
            case LIMIT_HOT:
                return Integer.parseInt(indexLimitCfg.getExtraValue2());
            case LIMIT_BRAND:
                return Integer.parseInt(indexLimitCfg.getExtraValue3());
            case LIMIT_TOPIC:
                return Integer.parseInt(indexLimitCfg.getExtraValue4());
            case LIMIT_CALLIST:
                return Integer.parseInt(indexLimitCfg.getExtraValue5());
            case LIMIT_CALGOOD:
                return Integer.parseInt(indexLimitCfg.getExtraValue6());
        }

        return -1;
    }


    private void inistConfigs() {
        for (LitemallSystem cfg : systemConfigs) {
            if ("index_banner".equals(cfg.getGroup()) && "new".equals(cfg.getKey()))
                newBannerCfg = cfg;

            if ("index_banner".equals(cfg.getGroup()) && "hot".equals(cfg.getKey()))
                hotBannerCfg = cfg;

            if ("index_limit".equals(cfg.getGroup()))
                indexLimitCfg = cfg;
        }
    }
}
