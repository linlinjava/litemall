package org.linlinjava.litemall.core.express.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 快递查询服务的配置属性，对应 {@code litemall.express} 配置项。
 */
@ConfigurationProperties(prefix = "litemall.express")
public class ExpressProperties {
    private boolean enable;
    private String appId;
    private String appKey;
    private List<Map<String, String>> vendors = new ArrayList<>();

    /**
     * 返回是否启用快递查询。
     *
     * @return 启用时为 {@code true}
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * 设置是否启用快递查询。
     *
     * @param enable 是否启用
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * 返回快递供应商列表。每项使用 {@code code} 和 {@code name} 分别表示供应商编码和名称。
     *
     * @return 快递供应商配置列表
     */
    public List<Map<String, String>> getVendors() {
        return vendors;
    }

    /**
     * 设置快递供应商列表。
     *
     * @param vendors 快递供应商配置列表，每项包含供应商编码和名称
     */
    public void setVendors(List<Map<String, String>> vendors) {
        this.vendors = vendors;
    }

    /**
     * 返回快递服务的应用密钥。
     *
     * @return 应用密钥
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * 设置快递服务的应用密钥。
     *
     * @param appKey 应用密钥
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    /**
     * 返回快递服务的应用标识。
     *
     * @return 应用标识
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 设置快递服务的应用标识。
     *
     * @param appId 应用标识
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }
}
