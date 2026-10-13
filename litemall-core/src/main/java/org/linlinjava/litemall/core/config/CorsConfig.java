package org.linlinjava.litemall.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域资源共享（CORS）配置类
 *
 * <p>小程序 / H5 前端与后端接口属于不同源（协议、域名、端口任一不同即跨域），
 * 浏览器会拦截跨域请求。这里通过注册一个全局 CorsFilter，对 {@code /**} 所有接口
 * 放行跨域预检与实际请求，使前端能够正常调用后端 API。</p>
 *
 * <p>注意：当前为方便演示，放行源、请求头、请求方法均为 "*"，生产环境应收紧为具体域名。</p>
 *
 * @author litemall
 * @since 1.0
 */
@Configuration
public class CorsConfig {
    /** 跨域预检请求（OPTIONS）的缓存时长，单位秒；设为 30 天，避免每次请求都发预检 */
    private long maxAge = 30 * 24 * 60 * 60;

    /**
     * 构造一条 CORS 规则：允许任意来源、任意请求头、任意 HTTP 方法，并携带凭证
     *
     * @return 配置好的 CorsConfiguration
     */
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");      // 允许任意访问源（生产环境建议改为具体域名）
        corsConfiguration.addAllowedHeader("*");     // 允许请求携带任意请求头
        corsConfiguration.addAllowedMethod("*");     // 允许 GET/POST/PUT/DELETE 等任意方法
        corsConfiguration.setMaxAge(maxAge);
        corsConfiguration.setAllowCredentials(true); // 允许携带 Cookie 等凭证信息
        return corsConfiguration;
    }

    /**
     * 将跨域规则注册到所有接口路径上，交由 Spring 过滤器链处理
     *
     * @return 全局跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }
}
