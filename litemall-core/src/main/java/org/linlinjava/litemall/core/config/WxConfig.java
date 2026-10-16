package org.linlinjava.litemall.core.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序与微信支付配置类
 *
 * <p>把 {@link WxProperties}（来自 application.yml 的 litemall.wx.* 配置）装配成
 * 小程序服务 {@link WxMaService} 和微信支付服务 {@link WxPayService} 两个 bean，
 * 供登录、解密用户数据、统一下单等业务直接注入使用。</p>
 *
 * @author litemall
 * @since 1.0
 */
@Configuration
public class WxConfig {
    @Autowired
    private WxProperties properties;

    /**
     * 小程序连接配置：appid、secret、消息校验 token、消息加解密密钥
     */
    @Bean
    public WxMaConfig wxMaConfig() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(properties.getAppId());
        config.setSecret(properties.getAppSecret());
        config.setToken(properties.getToken());
        config.setAesKey(properties.getAesKey());
        return config;
    }

    /** 小程序服务：封装 code 换 session、获取用户信息等能力 */
    @Bean
    public WxMaService wxMaService(WxMaConfig maConfig) {
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(maConfig);
        return service;
    }

    /**
     * 微信支付配置：商户号 mchId、商户密钥 mchKey、支付结果通知地址等，
     * 交易类型固定 JSAPI（小程序内支付），签名方式 MD5
     */
    @Bean
    public WxPayConfig wxPayConfig() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(properties.getAppId());
        payConfig.setMchId(properties.getMchId());
        payConfig.setMchKey(properties.getMchKey());
        payConfig.setNotifyUrl(properties.getNotifyUrl());
        payConfig.setKeyPath(properties.getKeyPath());
        payConfig.setTradeType("JSAPI");
        payConfig.setSignType("MD5");
        return payConfig;
    }

    /** 微信支付服务：封装统一下单、查询订单、退款等能力 */
    @Bean
    public WxPayService wxPayService(WxPayConfig payConfig) {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }
}
