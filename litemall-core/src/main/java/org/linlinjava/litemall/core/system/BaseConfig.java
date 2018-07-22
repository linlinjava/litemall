package org.linlinjava.litemall.core.system;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 配置基类，该类实际持有所有的配置，子类只是提供代理访问方法
 */
abstract class BaseConfig {
    //所有的配置均保存在该 HashMap 中
    protected static Map<String, String> configs = new HashMap<>();

    /**
     * 子类实现该方法，并告知父类配置前缀，该前缀用来索引配置组用于简化访问和按组重读配置
     *
     * @return
     */
    abstract String getPrefix();

    /**
     * 添加配置到公共Map中
     *
     * @param key
     * @param value
     */
    public static void addConfig(String key, String value) {
        configs.put(key, value);
    }

    /**
     * 重载配置,传入子类的prefix
     */
    public static void reloadConfig(String prefix) {
        //先遍历删除该 prefix 所有配置
        for (Iterator<Map.Entry<String, String>> it = configs.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> item = it.next();
            if (item.getKey().startsWith(prefix))
                it.remove();
        }

        ConfigService.getSystemConfigService().reloadConfig(prefix);
    }

    /**
     * 按String类型获取配置值
     *
     * @param keyName
     * @return
     */
    protected static String getConfig(String keyName) {
        return configs.get(keyName);
    }

    /**
     * 以Integer类型获取配置值
     *
     * @param keyName
     * @return
     */
    protected static Integer getConfigInt(String keyName) {
        return Integer.parseInt(configs.get(keyName));
    }

    /**
     * 以BigDecimal类型获取配置值
     *
     * @param keyName
     * @return
     */
    protected static BigDecimal getConfigBigDec(String keyName) {
        return new BigDecimal(configs.get(keyName));
    }
}
