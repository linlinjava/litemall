package org.linlinjava.litemall.wx.service;

import org.linlinjava.litemall.db.domain.LitemallSystem;
import org.linlinjava.litemall.db.service.LitemallSystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConfigService {
    private static ConfigService systemConfigService;

    //系统配置组
    private Map<String, String> systemConfig;

    @Autowired
    private LitemallSystemConfigService litemallSystemConfigService;

    //不允许实例化
    private ConfigService() {

    }

    /**
     * 获取系统配置
     *
     * @param keyName 例如 ： litemall.system.freight.value
     * @return 返回该值的字符串
     */
    public String getSysValue(String keyName) {
        keyName = "litemall.system." + keyName;
        return systemConfig.get(keyName);
    }

    /**
     * 获取系统配置数值型
     *
     * @param keyName 例如 ： litemall.system.freight.value
     * @return 返回该值的字符串
     */
    public Integer getSysValueInt(String keyName) {
        keyName = "litemall.system." + keyName;
        return Integer.parseInt(systemConfig.get(keyName));
    }


    /**
     * 获取系统设置实例
     *
     * @return
     */
    public static ConfigService getCfg() {
        return systemConfigService;
    }


    @PostConstruct
    public void inist() {
        systemConfigService = this;
        systemConfigService.systemConfig = new HashMap<>();
        systemConfigService.inistConfigs();
    }

    /**
     * 读取全部配置
     */
    private void inistConfigs() {
        List<LitemallSystem> list = litemallSystemConfigService.queryAll();
        for (LitemallSystem item : list) {
            //属于系统配置，放置到系统配置组
            if (item.getKeyName().startsWith("litemall.system"))
                systemConfig.put(item.getKeyName(), item.getKeyValue());
        }
    }
}
