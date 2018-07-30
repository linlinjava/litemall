package org.linlinjava.litemall.core.util;

import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

/**
 * 某些情况下外部依赖导致 yaml 配置未能正确注入时，@PropertySource(value = {"classpath:application-wx.yaml"}, factory = YmlPropertyFactory.class) 手动注入配置文件
 */
public class YmlPropertyFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        return name != null ? new PropertySourcesLoader().load(resource.getResource(), name, null) : new PropertySourcesLoader().load(
                resource.getResource(), getNameForResource(resource.getResource()), null);
    }

    private static String getNameForResource(Resource resource) {
        String name = resource.getDescription();
        if (!org.springframework.util.StringUtils.hasText(name)) {
            name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
        }
        return name;
    }
}
