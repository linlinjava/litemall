package org.linlinjava.litemall.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序 / 微信支付配置属性
 *
 * <p>将 application.yml 中前缀为 {@code litemall.wx} 的配置项绑定到本类字段，
 * 供 {@link WxConfig} 装配微信服务时读取。敏感信息（secret、mchKey）应放在配置文件或环境变量中，不要写死在代码里。</p>
 *
 * @author litemall
 * @since 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "litemall.wx")
public class WxProperties {

    /** 小程序唯一标识（appid） */
    private String appId;

    /** 小程序 appSecret，用于 code 换 session_key */
    private String appSecret;

    /** 微信支付商户号 mch-id */
    private String mchId;

    /** 微信支付商户密钥 mch-key */
    private String mchKey;

    /** 支付结果异步通知地址 notify-url */
    private String notifyUrl;

    /** 商户证书本地路径 key-path */
    private String keyPath;

    /** 小程序服务器消息校验 token */
    private String token;

    /** 小程序消息加解密 aes-key */
    private String aesKey;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getMchKey() {
        return mchKey;
    }

    public void setMchKey(String mchKey) {
        this.mchKey = mchKey;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }
}
