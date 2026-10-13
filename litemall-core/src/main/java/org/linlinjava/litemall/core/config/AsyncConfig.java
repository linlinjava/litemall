package org.linlinjava.litemall.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 异步任务配置类
 *
 * <p>通过 {@code @EnableAsync} 开启 Spring 对方法级异步调用的支持，
 * 使发短信、发邮件、推送模板消息等非核心耗时操作可以从主请求线程中剥离、
 * 在独立线程池中后台执行，避免阻塞小程序接口的响应。</p>
 *
 * @author litemall
 * @since 1.0
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
