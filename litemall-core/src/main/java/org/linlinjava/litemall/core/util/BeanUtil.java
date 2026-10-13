package org.linlinjava.litemall.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 容器工具类，用于在非 Spring 管理的类中获取 Bean 实例。
 */
@Component
public class BeanUtil implements ApplicationContextAware {
    /** 保存 Spring 容器上下文 */
    protected static ApplicationContext context;

    /**
     * 注入 Spring 容器上下文。
     *
     * @param applicationContext Spring 容器上下文
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 根据 Bean 名称获取 Bean 实例。
     *
     * @param name Bean 名称
     * @return Bean 实例
     */
    public static Object getBean(String name) {
        return context.getBean(name);
    }

    /**
     * 根据 Bean 类型获取 Bean 实例。
     *
     * @param c   Bean 类型
     * @param <T> Bean 类型泛型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> c){
        return context.getBean(c);
    }
}
