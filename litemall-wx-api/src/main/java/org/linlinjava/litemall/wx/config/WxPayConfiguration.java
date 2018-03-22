package org.linlinjava.litemall.wx.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Binary Wang
 */
@Configuration
@ConditionalOnClass(WxPayService.class)
@EnableConfigurationProperties(WxPayProperties.class)
public class WxPayConfiguration {
  @Autowired
  private WxPayProperties properties;

  @Bean
  @ConditionalOnMissingBean
  public WxPayConfig config() {
    WxPayConfig payConfig = new WxPayConfig();
    payConfig.setAppId(this.properties.getAppId());
    payConfig.setMchId(this.properties.getMchId());
    payConfig.setMchKey(this.properties.getMchKey());
    payConfig.setSubAppId(StringUtils.trimToNull(this.properties.getSubAppId()));
    payConfig.setSubMchId(StringUtils.trimToNull(this.properties.getSubMchId()));
    payConfig.setKeyPath(this.properties.getKeyPath());

    return payConfig;
  }

  @Bean
  //@ConditionalOnMissingBean
  public WxPayService wxPayService(WxPayConfig payConfig) {
    WxPayService wxPayService = new WxPayServiceImpl();
    wxPayService.setConfig(payConfig);
    return wxPayService;
  }

}
