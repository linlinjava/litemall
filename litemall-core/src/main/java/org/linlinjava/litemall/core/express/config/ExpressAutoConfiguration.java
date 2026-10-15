package org.linlinjava.litemall.core.express.config;

import org.linlinjava.litemall.core.express.ExpressService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 将物流配置绑定到 ExpressService，供订单查询复用统一服务。
 * <p>服务 Bean 始终创建，是否发送外部请求由 ExpressService 的 enable 检查决定。
 */
@Configuration
@EnableConfigurationProperties(ExpressProperties.class)
public class ExpressAutoConfiguration {

    private final ExpressProperties properties;

    public ExpressAutoConfiguration(ExpressProperties properties) {
        this.properties = properties;
    }

    /**
     * 将配置交给物流服务，在查询时统一判断启用状态。
     * @return 已绑定配置的物流查询服务
     */
    @Bean
    public ExpressService expressService() {
        ExpressService expressService = new ExpressService();
        expressService.setProperties(properties);
        return expressService;
    }

}
