package org.linlinjava.litemall.core.express.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 绑定 litemall.express 配置，集中管理物流开关、查询凭据与快递公司列表。
 * <p>enable 默认 false，vendors 默认空列表；启用前需配置服务商所需凭据。
 */
@ConfigurationProperties(prefix = "litemall.express")
public class ExpressProperties {
    /** 是否启用外部物流查询，未配置时为 false。 */
    private boolean enable;
    /** 快递鸟商户标识，用于请求中的 EBusinessID。 */
    private String appId;
    /** 快递鸟签名密钥，仅由部署配置注入。 */
    private String appKey;
    /** 快递公司配置，每项含 code 与 name，默认空列表。 */
    private List<Map<String, String>> vendors = new ArrayList<>();

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<Map<String, String>> getVendors() {
        return vendors;
    }

    public void setVendors(List<Map<String, String>> vendors) {
        this.vendors = vendors;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
